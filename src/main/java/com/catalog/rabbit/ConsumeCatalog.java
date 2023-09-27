package com.catalog.rabbit;

import com.catalog.article.Article;
import com.catalog.article.ArticleRepository;
import com.catalog.article.vo.ArticleData;
import com.catalog.rabbit.dto.EventArticleData;
import com.catalog.rabbit.dto.EventArticleExist;
import com.catalog.security.TokenService;
import com.catalog.utils.errors.ValidationError;
import com.catalog.utils.rabbit.DirectConsumer;
import com.catalog.utils.rabbit.RabbitEvent;
import com.catalog.utils.server.CatalogLogger;
import com.catalog.utils.server.Env;
import com.catalog.utils.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsumeCatalog {
    @Autowired
    CatalogLogger logger;

    @Autowired
    Env env;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    PublishArticleValidation publishArticleValidation;

    @Autowired
    PublishArticleData publishArticleData;

    @Autowired
    TokenService tokenService;

    public void init() {
        DirectConsumer directConsumer = new DirectConsumer(env, "catalog", "catalog");
        directConsumer.addProcessor("article-exist", this::processArticleExist);
        directConsumer.addProcessor("article-data", this::processArticleData);
        directConsumer.start();
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
            publishArticleValidation.publish(event.exchange, event.queue, exist);
        } catch (ValidationError validation) {
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

            Validator.validate(exist);

            ArticleData article = articleRepository.findById(exist.articleId).orElseThrow().data();
            EventArticleData data = new EventArticleData();
            data.articleId = article.id;
            data.price = article.price;
            data.referenceId = exist.referenceId;
            data.stock = article.stock;
            data.valid = article.enabled;

            publishArticleData.publish(event.exchange, event.queue, data);
        } catch (ValidationError validation) {
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