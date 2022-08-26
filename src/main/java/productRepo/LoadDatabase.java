package productRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	@Bean
	CommandLineRunner initDatabase(ProductRepository repository) {

		return args -> {
			log.info("Preloading " + repository.save(new Product("Product 1", 99, new Long[] {1L}, 1L)));
			log.info("Preloading " + repository.save(new Product("Product 2", 100, new Long[] {1L}, 2L)));
		};
	}
}
