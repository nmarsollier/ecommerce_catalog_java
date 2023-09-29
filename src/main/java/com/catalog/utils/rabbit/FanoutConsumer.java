package com.catalog.utils.rabbit;

import com.catalog.server.ValidatorService;
import com.catalog.server.CatalogLogger;
import com.catalog.server.EnvironmentVars;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Las colas fanout son un broadcast, no necesitan queue, solo exchange que es donde se publican
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FanoutConsumer {
    @Autowired
    EnvironmentVars environmentVars;

    @Autowired
    CatalogLogger logger;

    @Autowired
    ValidatorService validator;

    private String exchange;
    private final Map<String, EventProcessor> listeners = new HashMap<>();

    public FanoutConsumer init(String exchange) {
        this.exchange = exchange;
        return this;
    }

    public FanoutConsumer addProcessor(String event, EventProcessor listener) {
        listeners.put(event, listener);
        return this;
    }

    /**
     * En caso de desconexión se conectara nuevamente en 10 segundos
     */
    public void startDelayed() {
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                start();
            }
        }, 10 * 1000); // En 10 segundos reintenta.
    }

    /**
     * Conecta a rabbit para escuchar eventos
     */
    public void start() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(environmentVars.envData.rabbitServerUrl);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(exchange, "fanout");
            String queueName = channel.queueDeclare("", false, false, false, null).getQueue();

            channel.queueBind(queueName, exchange, "");

            new Thread(() -> {
                try {
                    logger.info("RabbitMQ Fanout Conectado");

                    Consumer consumer = new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag, //
                                                   Envelope envelope, //
                                                   AMQP.BasicProperties properties, //
                                                   byte[] body) {
                            try {
                                RabbitEvent event = RabbitEvent.fromJson(new String(body));
                                validator.validate(event);

                                EventProcessor eventConsumer = listeners.get(event.type);
                                if (eventConsumer != null) {
                                    logger.info("RabbitMQ Consume " + event.type);

                                    eventConsumer.process(event);
                                }
                            } catch (Exception e) {
                                logger.error("RabbitMQ Logout", e);
                            }
                        }
                    };
                    channel.basicConsume(queueName, true, consumer);
                } catch (Exception e) {
                    logger.info("RabbitMQ ArticleValidation desconectado");
                    startDelayed();
                }
            }).start();
        } catch (Exception e) {
            logger.error("RabbitMQ ArticleValidation desconectado", e);
            startDelayed();
        }
    }
}
