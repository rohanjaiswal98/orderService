package orderRepo;


import orderRepo.model.OrderDetails;
import orderRepo.model.Product;
import orderRepo.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderControllerTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    @Spy
    OrderController orderController;

    private OrderDetails orderDetails;
    private List<OrderDetails> orderDetailsList;

    @BeforeEach
    public void setup() {
        orderDetails = new OrderDetails("TestShippingAddress", 199, Map.of(1L, 1L), "testUser");
        orderDetailsList = Collections.singletonList(orderDetails);
    }

    @Test
    void getOrders() {
        when(orderRepository.findAll()).thenReturn(orderDetailsList);
        List<OrderDetails> actualResult = orderController.getOrders();
        assertEquals(actualResult, orderDetailsList);
    }

    @Test
    void newOrder() {
        ReflectionTestUtils.setField(orderController, "currentUser", "testUser");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + "testToken");
        ReflectionTestUtils.setField(orderController, "headers", httpHeaders);
        Product product = new Product("testProductName", "testDescription", 99);
        ResponseEntity<Product> responseEntity = ResponseEntity.ok(product);
        when(restTemplate.exchange(anyString(),
                eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<?>>any(),
                ArgumentMatchers.<Class<Product>>any()))
                .thenReturn(responseEntity);
        when(orderController.getRestTemplate()).thenReturn(restTemplate);
        when(orderRepository.save(any())).thenReturn(orderDetails);
        OrderDetails actualResult = orderController.newOrder(orderDetails);
        assertEquals(actualResult, orderDetails);
    }

    @Test
    void getOrderDetail() {
        Optional<OrderDetails> orderDetailsOptional = Optional.of(orderDetails);
        when(orderRepository.findById(anyLong())).thenReturn(orderDetailsOptional);
        OrderDetails actualResult = orderController.getOrderDetail(1L);
        assertEquals(actualResult, orderDetails);
    }

    @Test
    void updateOrder() {
        Optional<OrderDetails> orderDetailsOptional = Optional.of(orderDetails);
        when(orderRepository.findById(anyLong())).thenReturn(orderDetailsOptional);
        ReflectionTestUtils.setField(orderController, "currentUser", "testUser");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + "testToken");
        ReflectionTestUtils.setField(orderController, "headers", httpHeaders);
        Product product = new Product("testProductName", "testDescription", 99);
        ResponseEntity<Product> responseEntity = ResponseEntity.ok(product);
        when(restTemplate.exchange(anyString(),
                eq(HttpMethod.GET),
                ArgumentMatchers.<HttpEntity<?>>any(),
                ArgumentMatchers.<Class<Product>>any()))
                .thenReturn(responseEntity);
        when(orderController.getRestTemplate()).thenReturn(restTemplate);
        when(orderRepository.save(any())).thenReturn(orderDetails);
        OrderDetails actualResult = orderController.updateOrder(orderDetails, 1L);
        assertEquals(actualResult, orderDetails);

    }

    @Test
    void deleteOrder() {
        Optional<OrderDetails> orderDetailsOptional = Optional.of(orderDetails);
        when(orderRepository.findById(anyLong())).thenReturn(orderDetailsOptional);
        orderController.deleteOrder(1L);
        verify(orderRepository, times(1)).deleteById(anyLong());
    }
}