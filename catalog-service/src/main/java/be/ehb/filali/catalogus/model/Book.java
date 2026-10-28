package be.ehb.filali.catalogus.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** Beschrijft een boek in de catalogus. */
@Entity
public class Book {
    @Id
    private String isbn;

    @NotBlank
    private String title;

    private String authors;
    private String description;

    @DecimalMin(value = "0.0")
    private float price;

    @Min(value = 0)
    private int stock;

    public Book() {
    }

    public Book(String isbn, String title, String authors, String description, float price, int stock) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
