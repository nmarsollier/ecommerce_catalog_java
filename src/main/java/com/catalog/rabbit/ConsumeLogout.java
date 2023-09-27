package com.catalog.rabbit;

import com.catalog.security.TokenService;
import com.catalog.utils.rabbit.FanoutConsumer;
import com.catalog.utils.rabbit.RabbitEvent;
import com.catalog.utils.server.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsumeLogout {
    @Autowired
    Env env;

    @Autowired
    TokenService tokenService;

    public void init() {
        FanoutConsumer fanoutConsumer = new FanoutConsumer(env, "auth");
        fanoutConsumer.addProcessor("logout", this::processLogout);
        fanoutConsumer.start();
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
}