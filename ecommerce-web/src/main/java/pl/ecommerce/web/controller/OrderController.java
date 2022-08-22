package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.ecommerce.data.domain.Order;
import pl.ecommerce.web.service.OrderService;

@Controller
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;


    @GetMapping ("/{id}")
    public String showOrder(@PathVariable Long id, Model model) {

        Order order = orderService.findOrder(id);
        model.addAttribute("order", order);

        return "orders/show";
    }
}
