package com.catalog.article;

import com.catalog.CommonMocks;
import com.catalog.article.dto.ArticleData;
import com.catalog.rest.dto.NewData;
import com.catalog.utils.errors.ValidationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ArticleTest {

    @Test
    public void testNewArticleFromNewData() {
        NewData newData = CommonMocks.newDataExample;

        Article article = Article.newArticle(
                newData.name,
                newData.description,
                newData.image,
                newData.price,
                newData.stock
        );

        Assertions.assertEquals("Article Name", article.name);
        Assertions.assertEquals("Article Description", article.description);
        Assertions.assertEquals("image", article.image);
        Assertions.assertEquals(10.5, article.price);
        Assertions.assertEquals(20, article.stock);
        Assertions.assertNotNull(article.updated);
        Assertions.assertNotNull(article.created);
        Assertions.assertTrue(article.enabled);

        ArticleData data = article.data();
        Assertions.assertEquals("Article Name", data.name);
        Assertions.assertEquals("Article Description", data.description);
        Assertions.assertEquals("image", data.image);
        Assertions.assertEquals(10.5, data.price);
        Assertions.assertEquals(20, data.stock);
        Assertions.assertTrue(data.enabled);
    }

    @Test
    public void testUpdateDetails() {
        Article article = CommonMocks.articleExample;

        article.updateDetails("New name", "New description", "123");
        Assertions.assertEquals("New name", article.name);
        Assertions.assertEquals("New description", article.description);
        Assertions.assertEquals("123", article.image);

        article.updateDetails("Another name", "", "");
        Assertions.assertEquals("Another name", article.name);
        Assertions.assertEquals("", article.description);
        Assertions.assertEquals("", article.image);

        ValidationError error = Assertions.assertThrows(ValidationError.class,
                () -> article.updateDetails("", "", "")
        );
        Assertions.assertEquals(1, error.messages.size());
        Assertions.assertEquals("name", error.messages.get(0).path);
        Assertions.assertEquals("No puede estar vacío.", error.messages.get(0).message);
    }

    @Test
    public void testUpdatePrice() {
        Article article = CommonMocks.articleExample;

        article.updatePrice(35.23);
        Assertions.assertEquals(35.23, article.price);

        article.updatePrice(0);
        Assertions.assertEquals(0, article.price);

        ValidationError error = Assertions.assertThrows(ValidationError.class,
                () -> article.updatePrice(-1)
        );
        Assertions.assertEquals(1, error.messages.size());
        Assertions.assertEquals("price", error.messages.get(0).path);
        Assertions.assertEquals("Debe ser mayor a 0.", error.messages.get(0).message);
    }

    @Test
    public void testUpdateStock() {
        Article article = CommonMocks.articleExample;

        article.updateStock(35);
        Assertions.assertEquals(35, article.stock);

        article.updateStock(0);
        Assertions.assertEquals(0, article.stock);

        ValidationError error = Assertions.assertThrows(ValidationError.class,
                () -> article.updateStock(-1)
        );
        Assertions.assertEquals(1, error.messages.size());
        Assertions.assertEquals("stock", error.messages.get(0).path);
        Assertions.assertEquals("Debe ser mayor a 0.", error.messages.get(0).message);
    }

    @Test
    public void testDisable() {
        Article article = CommonMocks.articleExample;

        article.disable();
        Assertions.assertFalse(article.enabled);
    }
}
