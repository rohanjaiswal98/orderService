package productRepo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
class Product {

    private @Id
    @GeneratedValue Long id;
    private Long userId;
    private String shippingAddress;
    private float totalAmount;
    private Long[] productIds;

    Product() {
    }

    public Product(String shippingAddress, float totalAmount, Long[] productIds, Long userId) {
        this.shippingAddress = shippingAddress;
        this.totalAmount = totalAmount;
        this.productIds = productIds;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long[] getProductIds() {
        return productIds;
    }

    public void setProductIds(Long[] productIds) {
        this.productIds = productIds;
    }

    public Long getId() {
        return this.id;
    }

    public String getShippingAddress() {
        return this.shippingAddress;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Product))
            return false;
        Product product = (Product) o;
        return Objects.equals(this.id, product.id) && Objects.equals(this.shippingAddress, product.shippingAddress);
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.shippingAddress);
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + this.id + ", name='" + this.shippingAddress + '\'' +  ", price='" + this.totalAmount;
    }
}
