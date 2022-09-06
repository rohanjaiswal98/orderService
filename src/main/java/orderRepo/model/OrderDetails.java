package orderRepo.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Map;
import java.util.Objects;

@Entity
public class OrderDetails {

    private @Id
    @GeneratedValue Long id;
    private String username;
    @ElementCollection
    private Map<Long, Long> orderItems;
    private String shippingAddress;
    private float totalAmount;

    OrderDetails() {
    }

    public OrderDetails(String shippingAddress, float totalAmount, Map<Long, Long> orderItems, String username) {
        this.shippingAddress = shippingAddress;
        this.totalAmount = totalAmount;
        this.orderItems = orderItems;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<Long, Long> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Map<Long, Long> orderItems) {
        this.orderItems = orderItems;
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
        if (!(o instanceof OrderDetails))
            return false;
        OrderDetails order = (OrderDetails) o;
        return Objects.equals(this.id, order.id) && Objects.equals(this.shippingAddress, order.shippingAddress);
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
        return "Order{" + "id=" + this.id + ", name='" + this.shippingAddress + '\'' + ", price='" + this.totalAmount + "}";
    }
}
