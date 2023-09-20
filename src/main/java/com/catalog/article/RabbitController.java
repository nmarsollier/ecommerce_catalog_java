package com.catalog.article;

import com.catalog.article.vo.ArticleData;
import com.catalog.security.TokenService;
import com.catalog.utils.errors.ValidationError;
import com.catalog.utils.gson.Builder;
import com.catalog.utils.gson.JsonSerializable;
import com.catalog.utils.rabbit.*;
import com.catalog.utils.server.Env;
import com.catalog.utils.validator.Required;
import com.catalog.utils.validator.Validator;
import com.google.gson.annotations.SerializedName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class RabbitController {
    @Autowired
    Env env;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    TokenService tokenService;

    public void init() {
        FanoutConsumer fanoutConsumer = new FanoutConsumer(env, "auth");
        fanoutConsumer.addProcessor("logout", e -> processLogout(e));
        fanoutConsumer.start();

        DirectConsumer directConsumer = new DirectConsumer(env, "catalog", "catalog");
        directConsumer.addProcessor("article-exist", e -> processArticleExist(e));
        directConsumer.addProcessor("article-data", e -> processArticleData(e));
        directConsumer.start();

        TopicConsumer topicConsumer = new TopicConsumer(env, "sell_flow", "topic_catalog", "order_placed");
        topicConsumer.addProcessor("order-placed", e -> processOrderPlaced(e));
        topicConsumer.start();
    }

    /**
     * @api {fanout} auth/logout Logout
     * @apiGroup RabbitMQ GET
     * @apiDescription Escucha de mensajes logout desde auth. Invalida sesiones en cache.
     * @apiExample {json} Mensaje
     * {
     * "type": "article-exist",
     * "message" : "tokenId"
     * }
     */
    void processLogout(RabbitEvent event) {
        tokenService.invalidate(event.message.toString());
    }

    /**
     * @api {direct} catalog/article-exist Validación de Artículos
     * @apiGroup RabbitMQ GET
     * @apiDescription Escucha de mensajes article-exist desde cart. Valida artículos
     * @apiExample {json} Mensaje
     * {
     * "type": "article-exist",
     * "exchange" : "{Exchange name to reply}"
     * "queue" : "{Queue name to reply}"
     * "message" : {
     * "referenceId": "{redId}",
     * "articleId": "{articleId}",
     * }
     */
    void processArticleExist(RabbitEvent event) {
        EventArticleExist exist = EventArticleExist.fromJson(event.message.toString());
        try {
            System.out.println("RabbitMQ Consume article-exist : " + exist.articleId);

            Validator.validate(exist);
            Article article = articleRepository.findById(exist.articleId).orElseThrow();
            exist.valid = article.enabled();
            sendArticleValidation(event, exist);
        } catch (ValidationError validation) {
            exist.valid = false;
            sendArticleValidation(event, exist);
        } catch (Exception article) {
            return;
        }
    }

    /**
     * @api {direct} catalog/article-data Validación de Artículos
     * @apiGroup RabbitMQ GET
     * @apiDescription Escucha de mensajes article-data desde cart. Valida artículos
     * @apiExample {json} Mensaje
     * {
     * "type": "article-exist",
     * "exchange" : "{Exchange name to reply}"
     * "queue" : "{Queue name to reply}"
     * "message" : {
     * "referenceId": "{redId}",
     * "articleId": "{articleId}"
     * }
     */
    void processArticleData(RabbitEvent event) {
        EventArticleExist exist = EventArticleExist.fromJson(event.message.toString());
        try {
            System.out.println("RabbitMQ Consume article-data : " + exist.articleId);

            Validator.validate(exist);
            ArticleData article = articleRepository.findById(exist.articleId).orElseThrow().toArticleData();

            EventArticleData data = new EventArticleData();
            data.articleId = article.id;
            data.price = article.price;
            data.referenceId = exist.referenceId;
            data.stock = article.stock;
            data.valid = article.enabled;

            sendArticleData(event, data);
        } catch (ValidationError validation) {
            EventArticleData data = new EventArticleData();
            data.articleId = exist.articleId;
            data.referenceId = exist.referenceId;
            data.valid = false;
            sendArticleData(event, data);
        } catch (Exception article) {
            return;
        }
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
                    ArticleData article = articleRepository.findById(a.articleId).orElseThrow().toArticleData();

                    EventArticleData data = new EventArticleData();
                    data.articleId = article.id;
                    data.price = article.price;
                    data.referenceId = exist.orderId;
                    data.stock = article.stock;
                    data.valid = article.enabled;

                    sendArticleData(event, data);
                } catch (ValidationError validation) {
                    EventArticleData data = new EventArticleData();
                    data.articleId = a.articleId;
                    data.referenceId = exist.orderId;
                    data.valid = false;
                    sendArticleData(event, data);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * @api {direct} cart/article-exist Validación de Artículos
     * @apiGroup RabbitMQ POST
     * @apiDescription Enviá de mensajes article-exist desde cart. Valida artículos
     * @apiSuccessExample {json} Mensaje
     * {
     * "type": "article-exist",
     * "message" : {
     * "cartId": "{cartId}",
     * "articleId": "{articleId}",
     * "valid": True|False
     * }
     * }
     */
    private void sendArticleValidation(RabbitEvent event, EventArticleExist send) {
        RabbitEvent eventToSend = new RabbitEvent();
        eventToSend.type = "article-exist";
        eventToSend.message = send;

        DirectPublisher.publish(env, event.exchange, event.queue, eventToSend);
    }

    /**
     * @api {direct} cart/article-exist Validación de Articulos
     * @apiGroup RabbitMQ POST
     * @apiDescription Enviá de mensajes article-data desde cart. Valida articulos
     * @apiSuccessExample {json} Mensaje
     * {
     * "type": "article-data",
     * "message" : {
     * "cartId": "{cartId}",
     * "articleId": "{articleId}",
     * "valid": True|False,
     * "stock": {stock}
     * "price": {price}
     * }
     * }
     */
    public void sendArticleData(RabbitEvent event, EventArticleData send) {
        RabbitEvent eventToSend = new RabbitEvent();
        eventToSend.type = "article-data";
        eventToSend.message = send;

        DirectPublisher.publish(env, event.exchange, event.queue, eventToSend);
    }

    class EventArticleData implements JsonSerializable {
        @Required
        @SerializedName("articleId")
        public String articleId;
        @Required
        @SerializedName("referenceId")
        public String referenceId;
        @SerializedName("valid")
        public boolean valid;
        @SerializedName("price")
        public double price;
        @SerializedName("stock")
        public int stock;

        public static EventArticleExist fromJson(String json) {
            return Builder.gson().fromJson(json, EventArticleExist.class);
        }

        @Override
        public String toJson() {
            return Builder.gson().toJson(this);
        }
    }

    class EventArticleExist implements JsonSerializable {
        @Required
        @SerializedName("articleId")
        public String articleId;
        @Required
        @SerializedName("referenceId")
        public String referenceId;
        @SerializedName("valid")
        public boolean valid;

        public static EventArticleExist fromJson(String json) {
            return Builder.gson().fromJson(json, EventArticleExist.class);
        }

        @Override
        public String toJson() {
            return Builder.gson().toJson(this);
        }
    }

    private class OrderPlacedEvent {
        @SerializedName("orderId")
        public String orderId;
        @SerializedName("cartId")
        public String cartId;
        @SerializedName("articles")
        public Article[] articles;

        public static class Article {
            @SerializedName("articleId")
            @Required
            public String articleId;

            @SerializedName("quantity")
            @Required
            public int quantity;
        }

        public static OrderPlacedEvent fromJson(String json) {
            return Builder.gson().fromJson(json, OrderPlacedEvent.class);
        }
    }
}