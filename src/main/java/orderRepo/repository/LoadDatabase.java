package orderRepo.repository;

import orderRepo.model.OrderDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
class LoadDatabase {

	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	@Bean
	CommandLineRunner initDatabase(OrderRepository repository) {
		Map<Long, Long> productIds = new HashMap<>();
		productIds.put(1L, 2L);

		return args -> {
			log.info("Preloading " + repository.save(new OrderDetails("Product 1", 99, productIds, 1L)));
			log.info("Preloading " + repository.save(new OrderDetails("Product 2", 100, productIds, 2L)));
		};
	}
}
