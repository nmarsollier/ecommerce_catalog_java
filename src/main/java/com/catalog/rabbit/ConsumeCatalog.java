package com.catalog.rabbit;

import com.catalog.article.Article;
import com.catalog.article.ArticleRepository;
import com.catalog.article.dto.ArticleData;
import com.catalog.rabbit.dto.EventArticleData;
import com.catalog.rabbit.dto.EventArticleExist;
import com.catalog.server.ValidatorService;
import com.catalog.utils.rabbit.DirectConsumer;
import com.catalog.utils.rabbit.RabbitEvent;
import com.catalog.server.CatalogLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ConsumeCatalog {
    @Autowired
    ValidatorService validator;

    @Autowired
    CatalogLogger logger;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    PublishArticleValidation publishArticleValidation;

    @Autowired
    PublishArticleData publishArticleData;

    @Autowired
    DirectConsumer directConsumer;

    public void init() {
        directConsumer.init("catalog", "catalog")
                .addProcessor("article-exist", this::processArticleExist)
                .addProcessor("article-data", this::processArticleData)
                .start();
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

            Article article = articleRepository.findById(exist.articleId).orElseThrow();
            exist.valid = article.isEnabled();
            publishArticleValidation.publish(event.exchange, event.queue, exist);
        } catch (NoSuchElementException validation) {
            exist.valid = false;
            publishArticleValidation.publish(event.exchange, event.queue, exist);
        } catch (Exception article) {
            logger.error(article.toString(), article);
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

            ArticleData article = articleRepository
                    .findById(exist.articleId)
                    .orElseThrow()
                    .data();

            EventArticleData data = new EventArticleData();
            data.articleId = article.id;
            data.price = article.price;
            data.referenceId = exist.referenceId;
            data.stock = article.stock;
            data.valid = article.enabled;

            publishArticleData.publish(event.exchange, event.queue, data);
        } catch (NoSuchElementException validation) {
            EventArticleData data = new EventArticleData();
            data.articleId = exist.articleId;
            data.referenceId = exist.referenceId;
            data.valid = false;

            publishArticleData.publish(event.exchange, event.queue, data);
        } catch (Exception article) {
            logger.error(article.toString(), article);
        }
    }
}