package com.catalog.article;

import com.catalog.article.dto.ArticleData;
import com.catalog.utils.errors.Validate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.executable.ValidateOnExecution;
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
    public static Article newArticle(
            String name,
            String description,
            String image,
            double price,
            int stock
    ) {
        return new Article()
                .updateDetails(name, description, image)
                .updatePrice(price)
                .updateStock(stock);
    }

    /**
     * Actualiza la descripción de un articulo.
     */
    public Article updateDetails(@NotNull String name, @NotNull String description, @NotNull String image) {
        Validate.notEmpty("name", name);
        Validate.minLen("name", name, 1);
        Validate.maxLen("name", name, 50);
        Validate.maxLen("description", name, 2048);
        Validate.maxLen("image", name, 40);

        this.name = name;
        this.description = description;
        this.image = image;
        this.updated = new Date();

        return this;
    }

    /**
     * Actualiza el precio de un articulo.
     */
    @ValidateOnExecution
    public Article updatePrice(double price) {
        Validate.min("price", price, 0);

        this.price = price;
        this.updated = new Date();

        return this;
    }

    /**
     * Actualiza el stock actual de un articulo.
     */
    public Article updateStock(int stock) {
        Validate.min("stock", stock, 0);

        this.stock = stock;
        this.updated = new Date();

        return this;
    }

    public Article decrementStock(int stock) {
        int newStock = this.stock - stock;
        Validate.min("stock", newStock, 0);

        this.stock = newStock;
        this.updated = new Date();

        return this;
    }

    /**
     * Deshabilita el articulo para que no se pueda usar mas
     */
    public Article disable() {
        this.enabled = false;
        this.updated = new Date();
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Article storeIn(ArticleRepository repository) {
        repository.save(this);
        return this;
    }

    /**
     * Obtiene una representación interna de los valores.
     * Preserva la inmutabilidad de la entidad.
     */
    public ArticleData data() {
        return new ArticleData(
                this.id,
                this.name,
                this.description,
                this.image,
                this.price,
                this.stock,
                this.enabled
        );
    }
}
