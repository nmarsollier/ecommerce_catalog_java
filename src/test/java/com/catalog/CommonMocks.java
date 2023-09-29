package com.catalog;

import com.catalog.article.Article;
import com.catalog.rest.dto.NewData;

public class CommonMocks {
    public static NewData newDataExample;
    public static Article articleExample;

    static {
        newDataExample = new NewData(
                "Article Name",
                "Article Description",
                "image",
                10.5,
                20
        );

        articleExample = Article.newArticle(
                newDataExample.name,
                newDataExample.description,
                newDataExample.image, newDataExample.price,
                newDataExample.stock
        );
    }
}
