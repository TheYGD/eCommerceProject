package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.web.service.SellerService;

import javax.transaction.Transactional;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService sellerService;


    @GetMapping("/{id}")
    public String sellersProfile(@PathVariable Long id, Model model) {

        User seller = sellerService.getSeller(id);
        List<Product> productList = sellerService.getSellerProducts(seller);

        model.addAttribute("seller", seller);
        model.addAttribute("productList", productList);

        return "sellers/seller";
    }
}
