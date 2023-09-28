package com.catalog.utils.rabbit;

import com.catalog.utils.gson.GsonTools;
import com.catalog.utils.server.CatalogLogger;
import com.catalog.utils.server.EnvironmentVars;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Publicar en una cola direct es enviar un mensaje directo a un destinatario en particular,
 * Necesitamos un exchange y un queue especifico para enviar correctamente el mensaje.
 * Tanto el consumer como el publisher deben compartir estos mismos datos.
 */
@Service
public class DirectPublisher {
    @Autowired
    CatalogLogger logger;

    @Autowired
    EnvironmentVars environmentVars;

    public void publish(String exchange, String queue, RabbitEvent message) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(environmentVars.envData.rabbitServerUrl);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(exchange, "direct");
            channel.queueDeclare(queue, false, false, false, null);

            channel.queueBind(queue, exchange, queue);

            channel.basicPublish(exchange, queue, null, GsonTools.toJson(message).getBytes());

            logger.info("RabbitMQ Emit " + message.type);
        } catch (Exception e) {
            logger.error("RabbitMQ no se pudo encolar " + message.type, e);
        }
    }
}