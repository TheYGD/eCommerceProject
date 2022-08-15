package pl.ecommerce.ecommerceservice;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.ecommercedomain.Product;
import pl.ecommerce.ecommerceexceptions.exceptions.ItemNotFoundException;
import pl.ecommerce.ecommercerepository.ProductRepository;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;


    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow( () -> new ItemNotFoundException("No item found with id=" + id) );
    }

}
