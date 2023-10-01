package com.catalog.rabbit;

import com.catalog.article.Article;
import com.catalog.article.ArticleRepository;
import com.catalog.rabbit.dto.ConsumedOrderPlacedEvent;
import com.catalog.rabbit.dto.PublishArticleDataEvent;
import com.catalog.server.CatalogLogger;
import com.catalog.server.ValidatorService;
import com.catalog.utils.rabbit.RabbitEvent;
import com.catalog.utils.rabbit.TopicConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Service
public class ConsumeOrderPlaced {
    @Autowired
    ValidatorService validator;

    @Autowired
    CatalogLogger logger;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    PublishArticleData publishArticleData;

    @Autowired
    TopicConsumer topicConsumer;

    public void init() {
        topicConsumer.init("sell_flow", "topic_catalog", "order_placed")
                .addProcessor("order-placed", this::processOrderPlaced)
                .start();
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
            ConsumedOrderPlacedEvent exist = ConsumedOrderPlacedEvent.fromJson(event.message.toString());
            System.out.println("RabbitMQ Consume order-placed : " + exist.orderId);

            validator.validate(exist);

            Arrays.stream(exist.articles).forEach(a -> {
                try {
                    Article article = articleRepository.findById(a.articleId).orElseThrow();
                    article.decrementStock(a.quantity).storeIn(articleRepository);

                    PublishArticleDataEvent
                            .fromArticleData(article.data(), exist.orderId)
                            .publishIn(publishArticleData, event.exchange, event.queue);

                } catch (NoSuchElementException validation) {
                    PublishArticleDataEvent
                            .fromOrderPlacedEvent(a, exist.orderId)
                            .publishIn(publishArticleData, event.exchange, event.queue);
                }
            });
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }
}