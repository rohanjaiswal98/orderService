package orderRepo;

import orderRepo.exceptions.InternalServerError;
import orderRepo.exceptions.OrderNotFoundException;
import orderRepo.exceptions.ProductNotFoundException;
import orderRepo.exceptions.UserNotFoundException;
import orderRepo.model.OrderDetails;
import orderRepo.model.Product;
import orderRepo.model.User;
import orderRepo.repository.OrderRepository;
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
class OrderController {

    private final static String controllerPath = "/orders";

    private final OrderRepository repository;

    OrderController(OrderRepository repository) {
        this.repository = repository;
    }


    @GetMapping(controllerPath)
    List<OrderDetails> getOrders() {
        return repository.findAll();
    }

    private void fetchUser(Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String url = "http://localhost:8081/users/" + userId;
        headers.set("Authorization", "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.GET, entity, User.class).getBody();
    }

    private Product fetchProduct(Long productId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String url = "http://localhost:8082/products/" + productId;
        headers.set("Authorization", "Bearer " + SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, Product.class).getBody();
    }

    @PostMapping(controllerPath)
    OrderDetails newOrder(@RequestBody OrderDetails order) {
        Long userId = order.getUserId();
        AtomicReference<Float> totalAmount = new AtomicReference<>((float) 0);
        try {
            fetchUser(userId);

            order.getOrderItems().forEach((id, quantity) -> {
                Product product = fetchProduct(id);
                totalAmount.updateAndGet(v -> (v + product.getPrice() * quantity));
            });
            order.setTotalAmount(totalAmount.get());
            return repository.save(order);
        } catch (HttpClientErrorException e) {
            HttpStatus status = e.getStatusCode();
            if (status == HttpStatus.NOT_FOUND) {
                if (e.getResponseBodyAsString().contains("user"))
                    throw new UserNotFoundException(userId);
                throw new ProductNotFoundException(Long.valueOf(e.getResponseBodyAsString().split(":")[1]));
            } else if (status == HttpStatus.FORBIDDEN) {
                throw new InternalServerError("Forbidden");
            } else
                throw new InternalServerError("Some error occurred");
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
                    order.setUserId(updatedOrder.getUserId());
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
