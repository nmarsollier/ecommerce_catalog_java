package com.catalog.article;

import com.catalog.article.vo.ArticleData;
import com.catalog.article.vo.NewData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Es el Agregado principal de Articulo.
 */
@Document
public class Article {
    @Id
    String id;

    String name;
    String description;
    String image;
    double price;
    int stock;

    Date updated = new Date();
    Date created = new Date();

    boolean enabled = true;

    /**
     * Crea un nuevo articulo
     */
    public static Article newArticle(NewData data) {
        Article article = new Article();

        article.name = data.name;
        article.description = data.description;
        article.image = data.image;
        article.price = data.price;
        article.stock = data.stock;

        return article;
    }

    /**
     * Actualiza la descripción de un articulo.
     */
    public void updateDescription(NewData data) {
        this.name = data.name;
        this.description = data.description;
        this.image = data.image;
        this.updated = new Date();
    }

    /**
     * Actualiza el precio de un articulo.
     */
    public void updatePrice(double price) {
        this.price = price;
        this.updated = new Date();
    }

    /**
     * Actualiza el stock actual de un articulo.
     */
    public void updateStock(@Valid @Min(0) int stock) {
        this.stock = stock;
        this.updated = new Date();
    }

    /**
     * Deshabilita el articulo para que no se pueda usar mas
     */
    public void disable() {
        this.enabled = false;
        this.updated = new Date();
    }

    public boolean enabled() {
        return enabled;
    }

    /**
     * Obtiene una representación interna de los valores.
     * Preserva la inmutabilidad de la entidad.
     */
    public ArticleData data() {
        ArticleData data = new ArticleData();
        data.id = this.id;

        data.name = this.name;
        data.description = this.description;
        data.image = this.image;

        data.price = this.price;
        data.stock = this.stock;

        data.enabled = this.enabled;
        return data;
    }
}
