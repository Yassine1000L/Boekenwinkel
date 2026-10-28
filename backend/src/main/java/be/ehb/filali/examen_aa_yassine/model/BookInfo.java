package be.ehb.filali.examen_aa_yassine.model;

// Boekgegevens die de catalogusservice via REST terugstuurt.
public class BookInfo {
    private String isbn;
    private String title;
    private String authors;
    private String description;
    private float price;
    private int stock;

    public BookInfo() {
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
