package com.catalog.rabbit;

import com.catalog.article.ArticleRepository;
import com.catalog.article.vo.ArticleData;
import com.catalog.rabbit.dto.EventArticleData;
import com.catalog.rabbit.dto.OrderPlacedEvent;
import com.catalog.utils.rabbit.RabbitEvent;
import com.catalog.utils.rabbit.TopicConsumer;
import com.catalog.utils.server.CatalogLogger;
import com.catalog.utils.server.Env;
import com.catalog.utils.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Service
public class ConsumeOrderPlaced {
    @Autowired
    CatalogLogger logger;

    @Autowired
    Env env;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    PublishArticleData publishArticleData;

    public void init() {
        TopicConsumer topicConsumer = new TopicConsumer(env, "sell_flow", "topic_catalog", "order_placed");
        topicConsumer.addProcessor("order-placed", this::processOrderPlaced);
        topicConsumer.start();
    }

    /**
     * @api {topic} order/order-placed Orden Creada
     * @apiGroup RabbitMQ
     * @apiDescription Consume de mensajes order-placed desde Order con el topic "order_placed".
     * @apiSuccessExample {json} Mensaje
     * {
     * "type": "order-placed",
     * "message" : {
     * "cartId": "{cartId}",
     * "orderId": "{orderId}"
     * "articles": [{
     * "articleId": "{article id}"
     * "quantity" : {quantity}
     * }, ...]
     * }
     * }
     */
    void processOrderPlaced(RabbitEvent event) {
        try {
            OrderPlacedEvent exist = OrderPlacedEvent.fromJson(event.message.toString());
            System.out.println("RabbitMQ Consume order-placed : " + exist.orderId);

            Validator.validate(exist);

            Arrays.stream(exist.articles).forEach(a -> {
                try {
                    ArticleData article = articleRepository.findById(a.articleId).orElseThrow().data();

                    EventArticleData data = new EventArticleData();
                    data.articleId = article.id;
                    data.price = article.price;
                    data.referenceId = exist.orderId;
                    data.stock = article.stock;
                    data.valid = article.enabled;

                    publishArticleData.publish(event.exchange, event.queue, data);
                } catch (NoSuchElementException validation) {
                    EventArticleData data = new EventArticleData();
                    data.articleId = a.articleId;
                    data.referenceId = exist.orderId;
                    data.valid = false;
                    publishArticleData.publish(event.exchange, event.queue, data);
                }
            });
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }
}