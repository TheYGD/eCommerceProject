package pl.ecommerce.web.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.OrderDto;
import pl.ecommerce.data.mapper.OrderMapper;
import pl.ecommerce.repository.CartRepository;
import pl.ecommerce.repository.OrderRepository;
import pl.ecommerce.repository.SoldProductRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CheckoutService {

    private final OrderService orderService;


    public Long postOrder(UserCredentials userCredentials, OrderDto orderDto) {

        return orderService.postOrder(userCredentials, orderDto);
    }
}
