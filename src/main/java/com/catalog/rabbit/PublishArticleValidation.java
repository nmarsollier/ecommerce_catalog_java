package com.catalog.rabbit;

import com.catalog.rabbit.dto.EventArticleExist;
import com.catalog.utils.rabbit.DirectPublisher;
import com.catalog.utils.rabbit.RabbitEvent;
import com.catalog.utils.server.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service

public class PublishArticleValidation {
    @Autowired
    Env env;

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
    public void publish(String exchange, String queue, EventArticleExist send) {
        RabbitEvent eventToSend = new RabbitEvent();
        eventToSend.type = "article-exist";
        eventToSend.message = send;

        DirectPublisher.publish(env, exchange, queue, eventToSend);
    }
}
