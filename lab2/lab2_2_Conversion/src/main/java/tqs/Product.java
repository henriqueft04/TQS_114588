package tqs;

// Product class
public class Product {
    private Integer id;
    private String image;
    private String description;
    private Double price;
    private String title;
    private String category;

    public Product(Integer id, String image, String description, Double price, String title, String category) {
        this.id = id;
        this.image = image;
        this.description = description;
        this.price = price;
        this.title = title;
        this.category = category;
    }

    // Getters and setters (or can use Lombok annotations)
    public Integer getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }
}
