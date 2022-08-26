package productRepo;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
class ProductController {

    private final ProductRepository repository;

    ProductController(ProductRepository repository) {
        this.repository = repository;
    }


    @GetMapping("/products")
    CollectionModel<EntityModel<Product>> all() {

        List<EntityModel<Product>> users = repository.findAll().stream()
                .map(product -> EntityModel.of(product,
                        linkTo(methodOn(ProductController.class).one(product.getId())).withSelfRel(),
                        linkTo(methodOn(ProductController.class).all()).withRel("products")))
                .collect(Collectors.toList());

        return CollectionModel.of(users, linkTo(methodOn(ProductController.class).all()).withSelfRel());
    }

    @PostMapping("/products")
    Product newUser(@RequestBody Product product) {
        return repository.save(product);
    }


    @GetMapping("/products/{id}")
    EntityModel<Product> one(@PathVariable Long id) {

        Product product = repository.findById(id) //
                .orElseThrow(() -> new ProductNotFoundException(id));

        return EntityModel.of(product, //
                linkTo(methodOn(ProductController.class).one(id)).withSelfRel(),
                linkTo(methodOn(ProductController.class).all()).withRel("products"));
    }

    @PutMapping("/products/{id}")
    Product updateUser(@RequestBody Product newProduct, @PathVariable Long id) {

        return repository.findById(id) //
                .map(product -> {
                    product.setShippingAddress(newProduct.getShippingAddress());
                    product.setTotalAmount(newProduct.getTotalAmount());
                    return repository.save(product);
                }) //
                .orElseGet(() -> {
                    newProduct.setId(id);
                    return repository.save(newProduct);
                });
    }

    @DeleteMapping("/products/{id}")
    ResponseEntity<Product> deleteUser(@PathVariable Long id) {
        Product product = repository.findById(id).map(p -> {
            repository.deleteById(id);
            return p;
        }).orElseThrow(() -> new ProductNotFoundException(id));
        return new ResponseEntity<>(product, HttpStatus.OK);
    }
}
