package com.catalog.rabbit;

import com.catalog.rabbit.dto.EventArticleData;
import com.catalog.utils.rabbit.DirectPublisher;
import com.catalog.utils.rabbit.RabbitEvent;
import com.catalog.utils.server.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublishArticleData {
    @Autowired
    Env env;

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
    public void publish(String exchange, String queue, EventArticleData send) {
        RabbitEvent eventToSend = new RabbitEvent();
        eventToSend.type = "article-data";
        eventToSend.message = send;

        DirectPublisher.publish(env, exchange, queue, eventToSend);
    }
}
