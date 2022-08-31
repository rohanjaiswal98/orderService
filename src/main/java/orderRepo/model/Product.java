package orderRepo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Product {

    private @Id
    @GeneratedValue Long id;
    private String productName;
    private String description;
    private float price;

    Product() {
    }

    public Product(String productName, String description, float price) {
        this.productName = productName;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return this.id;
    }

    public String getProductName() {
        return this.productName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Product))
            return false;
        Product product = (Product) o;
        return Objects.equals(this.id, product.id) && Objects.equals(this.productName, product.productName)
                && Objects.equals(this.description, product.description);
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.productName, this.description);
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + this.id + ", name='" + this.productName + '\'' + ", description='" + this.description + '\'' + ", price='" + this.price;
    }
}
