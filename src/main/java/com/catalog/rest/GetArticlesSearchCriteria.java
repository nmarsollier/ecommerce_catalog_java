package com.catalog.rest;

import com.catalog.article.Article;
import com.catalog.article.ArticleRepository;
import com.catalog.article.vo.ArticleData;
import com.catalog.utils.StringTools;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping(value = "/v1")
public class GetArticlesSearchCriteria {
    @Autowired
    ArticleRepository repository;

    /**
     * @api {get} /v1/articles/search/:criteria Buscar Artículo
     * @apiName Buscar Artículo
     * @apiGroup Artículos
     * @apiDescription Busca artículos por nombre o descripción
     * @apiSuccessExample {json} Respuesta
     * HTTP/1.1 200 OK
     * [
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
     * },
     * ...
     * ]
     * @apiUse Errors
     */
    @GetMapping(
            value = "articles/search/{criteria}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<ArticleData> searchArticles(
            @NotEmpty @PathVariable("criteria") String criteria
    ) {
        return repository.findWithCriteria(StringTools.escapeForRegex(criteria))
                .stream()
                .map(Article::data)
                .collect(Collectors.toList());
    }
}
