package orderRepo;

import orderRepo.exceptions.ForbiddenException;
import orderRepo.exceptions.InternalServerError;
import orderRepo.exceptions.OrderNotFoundException;
import orderRepo.exceptions.ProductNotFoundException;
import orderRepo.model.OrderDetails;
import orderRepo.model.Product;
import orderRepo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@Configuration
class OrderController {

    private final static String controllerPath = "/orders";

    @Autowired
    private OrderRepository repository;

    private String currentUser;

    private HttpHeaders headers;

    @Autowired
    private RestTemplate restTemplate;

    public OrderController() {
        headers = new HttpHeaders();
    }

    @GetMapping(controllerPath)
    List<OrderDetails> getOrders() {
        return repository.findAll();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private Product fetchProduct(Long productId) {
        String url = "http://localhost:8082/products/" + productId;
        if (!headers.containsKey("Test"))
            headers.set("Authorization", "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, Product.class).getBody();
    }

    @PostMapping(controllerPath)
    OrderDetails newOrder(@RequestBody OrderDetails order) {
        String username = order.getUsername();
        AtomicReference<Float> totalAmount = new AtomicReference<>((float) 0);
        try {
            if (currentUser == null || SecurityContextHolder.getContext().getAuthentication() != null)
                currentUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

            if (!currentUser.contentEquals(username))
                throw new ForbiddenException("Cannot place order for " + username);

            order.getOrderItems().forEach((id, quantity) -> {
                Product product = fetchProduct(id);
                totalAmount.updateAndGet(v -> (v + product.getPrice() * quantity));
            });
            order.setTotalAmount(totalAmount.get());
            return repository.save(order);
        } catch (HttpClientErrorException e) {
            HttpStatus status = e.getStatusCode();
            if (status == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException(Long.valueOf(e.getResponseBodyAsString().split(":")[1]));
            } else if (status == HttpStatus.FORBIDDEN) {
                throw new ForbiddenException("Forbidden");
            } else
                throw new InternalServerError("Some error occurred");
        } catch (ForbiddenException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerError("Some error occurred");
        }
    }


    @GetMapping(controllerPath + "/{id}")
    OrderDetails getOrderDetail(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @PutMapping(controllerPath + "/{id}")
    OrderDetails updateOrder(@RequestBody OrderDetails updatedOrder, @PathVariable Long id) {

        return repository.findById(id)
                .map(order -> {
                    order.setUsername(updatedOrder.getUsername());
                    order.setOrderItems(updatedOrder.getOrderItems());
                    order.setShippingAddress(updatedOrder.getShippingAddress());
                    return newOrder(order);
                })
                .orElseGet(() -> newOrder(updatedOrder));
    }

    @DeleteMapping(controllerPath + "/{id}")
    void deleteOrder(@PathVariable Long id) {
        repository.findById(id).map(p -> {
            repository.deleteById(id);
            return p;
        }).orElseThrow(() -> new OrderNotFoundException(id));
    }
}
