package com.catalog.rest;

import com.catalog.article.Article;
import com.catalog.article.ArticleRepository;
import com.catalog.article.dto.ArticleData;
import com.catalog.rest.dto.NewData;
import com.catalog.security.ValidateAdminUser;
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
public class PostArticles {
    @Autowired
    ArticleRepository repository;

    /**
     * @api {post} /v1/articles/ Crear Artículo
     * @apiName Crear Artículo
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
            value = "/articles",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ArticleData addArticle(
            @ValidateAdminUser @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @Valid @RequestBody NewData newArticle
    ) {
        return Article.newArticle(
                newArticle.name,
                newArticle.description,
                newArticle.image,
                newArticle.price,
                newArticle.stock
        ).storeIn(repository).data();
    }
}
