package com.catalog.rabbit;

import com.catalog.article.Article;
import com.catalog.article.ArticleRepository;
import com.catalog.article.dto.ArticleData;
import com.catalog.rabbit.dto.PublishArticleDataEvent;
import com.catalog.rabbit.dto.ArticleExistEvent;
import com.catalog.server.CatalogLogger;
import com.catalog.server.ValidatorService;
import com.catalog.utils.rabbit.DirectConsumer;
import com.catalog.utils.rabbit.RabbitEvent;
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
        ArticleExistEvent exist = ArticleExistEvent.fromJson(event.message.toString());
        try {
            System.out.println("RabbitMQ Consume article-exist : " + exist.articleId);

            Article article = articleRepository.findById(exist.articleId).orElseThrow();

            ArticleExistEvent
                    .fromEventArticleExist(exist, article.isEnabled())
                    .publishIn(publishArticleValidation, event.exchange, event.queue);

        } catch (NoSuchElementException validation) {
            ArticleExistEvent
                    .fromEventArticleExist(exist, false)
                    .publishIn(publishArticleValidation, event.exchange, event.queue);
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
        ArticleExistEvent exist = ArticleExistEvent.fromJson(event.message.toString());
        try {
            System.out.println("RabbitMQ Consume article-data : " + exist.articleId);

            ArticleData article = articleRepository
                    .findById(exist.articleId)
                    .orElseThrow()
                    .data();

            PublishArticleDataEvent
                    .fromArticleData(article, exist.referenceId)
                    .publishIn(publishArticleData, event.exchange, event.queue);

        } catch (NoSuchElementException validation) {
            PublishArticleDataEvent
                    .fromEventArticleExist(exist)
                    .publishIn(publishArticleData, event.exchange, event.queue);
        } catch (Exception article) {
            logger.error(article.toString(), article);
        }
    }
}