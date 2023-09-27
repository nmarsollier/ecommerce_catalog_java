package com.catalog.rest;

import com.catalog.article.Article;
import com.catalog.article.ArticleRepository;
import com.catalog.article.vo.ArticleData;
import com.catalog.article.vo.NewData;
import com.catalog.security.TokenService;
import com.catalog.utils.errors.SimpleError;
import com.catalog.utils.errors.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/v1")
public class PostArticlesId {
    @Autowired
    ArticleRepository repository;

    @Autowired
    TokenService tokenService;

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
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @PathVariable("articleId") String articleId,
            @RequestBody NewData descriptionData
    ) throws SimpleError, ValidationError {
        tokenService.validateAdmin(auth);

        Article article = repository.findById(articleId).orElseThrow(
                () -> new ValidationError(404).addPath("articleId", "Not found")
        );
        article.updateDescription(descriptionData);

        article.updatePrice(descriptionData.price);
        article.updateStock(descriptionData.stock);

        repository.save(article);

        return article.data();
    }
}
