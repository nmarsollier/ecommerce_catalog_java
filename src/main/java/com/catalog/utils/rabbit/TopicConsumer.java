package com.catalog.utils.rabbit;

import com.catalog.utils.errors.ValidatorService;
import com.catalog.utils.server.CatalogLogger;
import com.catalog.utils.server.EnvironmentVars;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * La cola topic permite que varios consumidores escuchen el mismo evento
 * topic es muy importante por es el evento que se va a escuchar
 * Para que un consumer escuche los eventos debe estar conectado al mismo exchange y escuchar el topic adecuado
 * queue permite distribuir la carga de los mensajes entre distintos consumers, los consumers con el mismo queue name
 * comparten la carga de procesamiento de mensajes, es importante que se defina el queue
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TopicConsumer {
    @Autowired
    EnvironmentVars environmentVars;

    @Autowired
    final ValidatorService validator = new ValidatorService();

    @Autowired
    CatalogLogger logger;

    String exchange;
    String queue;
    String topic;
    private final Map<String, EventProcessor> listeners = new HashMap<>();


    public TopicConsumer init(String exchange, String queue, String topic) {
        this.exchange = exchange;
        this.queue = queue;
        this.topic = topic;
        return this;
    }

    public TopicConsumer addProcessor(String event, EventProcessor listener) {
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

            channel.exchangeDeclare(exchange, "topic");
            channel.queueDeclare(queue, false, false, false, null);
            channel.queueBind(queue, exchange, topic);

            new Thread(() -> {
                try {
                    logger.info("RabbitMQ Topic Conectado");

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
                    channel.basicConsume(queue, true, consumer);
                } catch (Exception e) {
                    logger.error("RabbitMQ ArticleValidation desconectado", e);
                    startDelayed();
                }
            }).start();
        } catch (Exception e) {
            logger.error("RabbitMQ ArticleValidation desconectado", e);
            startDelayed();
        }
    }
}
