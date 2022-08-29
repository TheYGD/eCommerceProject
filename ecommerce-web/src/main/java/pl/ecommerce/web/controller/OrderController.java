package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.data.domain.MessageCause;
import pl.ecommerce.data.domain.Order;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.data.other.StringResponse;
import pl.ecommerce.web.service.OrderService;

import java.util.List;
import java.util.Map;

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


    @GetMapping("/{id}/ask")
    public String askAboutOrderPage(@AuthenticationPrincipal UserCredentials userCredentials,
                                    @PathVariable("id") Long orderId, Model model) {

        List<MessageCause> messageCauseList = orderService.getOrderMessageTitles();
        Order order = orderService.findOrder(orderId);
        orderService.orderBelongsToUser(order, userCredentials); // validation

        model.addAttribute("messageCauseList", messageCauseList);
        model.addAttribute("order", order);

        return "messages/create-order";
    }


    @PostMapping("/{id}/ask")
    public String askAboutOrder(@AuthenticationPrincipal UserCredentials userCredentials,
                                @PathVariable("id") Long orderId,
                                @RequestParam Map<String, String> formData) {

        Long chatId = orderService.askAboutOrder(userCredentials, orderId, formData);

        return "redirect:/messages?id=" + chatId;
    }
}
