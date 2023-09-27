package com.catalog.rest;

import com.catalog.article.ArticleRepository;
import com.catalog.article.vo.ArticleData;
import com.catalog.utils.errors.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/v1")
public class GetArticleId {
    @Autowired
    ArticleRepository repository;

    /**
     * @api {get} /v1/articles/:articleId Buscar Artículo
     * @apiName Buscar Artículo
     * @apiGroup Articulos
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
     * "enabled": {activo}
     * }
     * @apiUse Errors
     */
    @GetMapping(
            value = "/articles/{articleId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ArticleData getArticle(
            @PathVariable("articleId") String articleId
    ) throws ValidationError {
        return repository.findById(articleId).orElseThrow(
                () -> new ValidationError(404).addPath("articleId", "Not found")
        ).data();
    }
}
