package com.catalog.rest;

import com.catalog.article.Article;
import com.catalog.article.ArticleRepository;
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
public class DeleteArticlesId {
    @Autowired
    ArticleRepository repository;

    @Autowired
    TokenService tokenService;

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
    ) throws SimpleError, ValidationError {
        tokenService.validateAdmin(auth);

        Article article = repository.findById(articleId).orElseThrow(
                () -> new ValidationError(404).addPath("articleId", "Not found")
        );
        article.disable();
        repository.save(article);
        return "";
    }
}
