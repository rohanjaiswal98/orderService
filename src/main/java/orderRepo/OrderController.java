package orderRepo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

    @PostMapping(controllerPath)
    OrderDetails newOrder(@RequestBody OrderDetails order) {
        Long userId = order.getUserId();
        RestTemplate restTemplate = new RestTemplate();
        User user = null;
        try {
            user = restTemplate.getForEntity("http://localhost:8080/users/" + userId, User.class).getBody();
            return repository.save(order);
        } catch (HttpClientErrorException e) {
            HttpStatus status = e.getStatusCode();
            if(status == HttpStatus.NOT_FOUND)
                throw new UserNotFoundException(userId);
            else
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
                    order.setTotalAmount(updatedOrder.getTotalAmount());
                    return repository.save(order);
                })
                .orElseGet(() -> repository.save(updatedOrder));
    }

    @DeleteMapping(controllerPath + "/{id}")
    ResponseEntity<OrderDetails> deleteOrder(@PathVariable Long id) {
        OrderDetails order = repository.findById(id).map(p -> {
            repository.deleteById(id);
            return p;
        }).orElseThrow(() -> new OrderNotFoundException(id));
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
