package com.catalog.rest;

import com.catalog.article.Article;
import com.catalog.article.ArticleRepository;
import com.catalog.article.vo.ArticleData;
import com.catalog.rest.dto.NewData;
import com.catalog.security.ValidateAdminUser;
import com.catalog.utils.errors.NotFoundError;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/v1")
@Validated
public class PostArticlesId {
    @Autowired
    ArticleRepository repository;

    /**
     * @api {post} /v1/articles/:articleId Actualizar Artículo
     * @apiName Actualizar Artículo
     * @apiGroup Artículos
     * @apiUse AuthHeader
     * @apiExample {json} Body
     * {
     * "name": "{nombre del articulo}",
     * "description": "{descripción del articulo}",
     * "image": "{id de imagen}",
     * "price": {precio actual},
     * "stock": {stock actual}
     * }
     * @apiSuccessExample {json} Respuesta
     * HTTP/1.1 200 OK
     * {
     * "_id": "{id de articulo}"
     * "name": "{nombre del articulo}",
     * "description": "{descripción del articulo}",
     * "image": "{id de imagen}",
     * "price": {precio actual},
     * "stock": {stock actual}
     * "updated": {fecha ultima actualización}
     * "created": {fecha creación}
     * "enabled": {si esta activo}
     * }
     * @apiUse Errors
     */
    @PostMapping(
            value = "/articles/{articleId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ArticleData updateArticle(
            @ValidateAdminUser @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @PathVariable("articleId") String articleId,
            @Valid @RequestBody NewData descriptionData
    ) {
        Article article = repository.findById(articleId).orElseThrow(
                () -> new NotFoundError("articleId")
        );

        article.updateDetails(
                descriptionData.name,
                descriptionData.description,
                descriptionData.image
        );

        article.updatePrice(descriptionData.price);
        article.updateStock(descriptionData.stock);

        repository.save(article);

        return article.data();
    }
}
