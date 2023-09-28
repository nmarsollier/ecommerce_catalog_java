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
 * La cola topic permite que varios consumidores escuchen el mismo evento
 * topic es muy importante por es el evento que se va a escuchar
 * Para que un consumer escuche los eventos debe estar conectado al mismo exchange y escuchar el topic adecuado
 * queue permite distribuir la carga de los mensajes entre distintos consumers, los consumers con el mismo queue name
 * comparten la carga de procesamiento de mensajes, es importante que se defina el queue
 */
@Service
public class TopicPublisher {
    @Autowired
    CatalogLogger logger;

    @Autowired
    EnvironmentVars environmentVars;

    public void publish(String exchange, String topic, RabbitEvent message) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(environmentVars.envData.rabbitServerUrl);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(exchange, "topic");

            channel.basicPublish(exchange, topic, null, GsonTools.toJson(message).getBytes());

            logger.info("RabbitMQ Emit " + message.type);
        } catch (Exception e) {
            logger.error("RabbitMQ no se pudo encolar " + message.type, e);
        }
    }
}
