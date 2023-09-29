package com.catalog.rest;

import com.catalog.article.ArticleRepository;
import com.catalog.security.ValidateAdminUser;
import com.catalog.utils.errors.NotFoundError;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/v1")
@Validated
public class DeleteArticlesId {
    @Autowired
    ArticleRepository repository;

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
            @ValidateAdminUser @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @NotEmpty @PathVariable("articleId") String articleId
    ) {
        repository.findById(articleId).orElseThrow(
                () -> new NotFoundError("articleId")
        ).disable().storeIn(repository);

        return "";
    }
}
