package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.repository.OrderRepository;
import pl.ecommerce.repository.ProductRepository;
import pl.ecommerce.repository.SoldProductsGroupRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ProfileService {

    private final SoldProductsGroupRepository soldProductsGroupRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    public List<SoldProductsGroup> getSoldProductsGroupList(UserCredentials userCredentials) {
        return soldProductsGroupRepository.findAllBySeller(userCredentials.getUserAccount());
    }

    public List<Order> getOrderedProducts(UserCredentials userCredentials) {
        return orderRepository.findAllByBuyer(userCredentials.getUserAccount());
    }

    public List<Product> getOwnProducts(UserCredentials userCredentials) {
        return productRepository.findAllBySeller(userCredentials.getUserAccount());
    }
}
