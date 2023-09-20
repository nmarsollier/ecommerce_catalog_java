package com.catalog.article;

import com.catalog.article.vo.ArticleData;
import com.catalog.article.vo.NewData;
import com.catalog.security.TokenService;
import com.catalog.utils.errors.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@org.springframework.web.bind.annotation.RestController
@RequestMapping(value = "/v1")
public class RestController {
    @Autowired
    ArticleRepository repository;

    @Autowired
    TokenService tokenService;

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
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody NewData newArticle
    ) {
        tokenService.validateAdmin(auth);

        Article article = Article.newArticle(newArticle);
        repository.save(article);

        return article.toArticleData();
    }

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
    ) {
        tokenService.validateAdmin(auth);

        Article article = repository.findById(articleId).orElseThrow(
                () -> new ValidationError(404).addPath("articleId", "Not found")
        );
        article.updateDescription(descriptionData);

        article.updatePrice(descriptionData.price);
        article.updateStock(descriptionData.stock);

        repository.save(article);

        return article.toArticleData();
    }

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
    ) {
        return repository.findById(articleId).orElseThrow(
                () -> new ValidationError(404).addPath("articleId", "Not found")
        ).toArticleData();
    }

    /**
     * @api {delete} /articles/:articleId Eliminar Artículo
     * @apiName Eliminar Artículo
     * @apiGroup Artículos
     * @apiUse AuthHeader
     * @apiSuccessExample {json} 200 Respuesta
     * HTTP/1.1 200 OK
     * @apiUse Errors
     */
    @DeleteMapping(
            value = "/articles/{articleId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String deleteArticle(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @PathVariable("articleId") String articleId
    ) {
        tokenService.validateAdmin(auth);

        Article article = repository.findById(articleId).orElseThrow(
                () -> new ValidationError(404).addPath("articleId", "Not found")
        );
        article.disable();
        repository.save(article);
        return "";
    }

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
            @PathVariable("criteria") String criteria
    ) {
        return repository.findWithCriteria(escapeForRegex(criteria))
                .stream()
                .map(article -> article.toArticleData())
                .collect(Collectors.toList());
    }

    private static String escapeForRegex(String input) {
        return input.replace("\\", "\\\\")
                .replace(".", "\\.")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace("?", "\\?")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("^", "\\^")
                .replace("$", "\\$")
                .replace("|", "\\|");
    }
}
