package orderRepo.repository;

import orderRepo.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderDetails, Long> {

}
