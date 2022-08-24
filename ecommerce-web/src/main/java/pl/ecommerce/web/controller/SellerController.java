package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.web.service.SellerService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService sellerService;


    @GetMapping("/{id}")
    public String sellersProfile(@PathVariable Long id, Model model, @RequestParam(defaultValue = "1") int pageNr,
                                 @RequestParam(defaultValue = "0") int sortOption) {

        User seller = sellerService.getSeller(id);
        Page<Product> productPage = sellerService.getSellerProducts(seller, pageNr, sortOption);

        model.addAttribute("seller", seller);
        model.addAttribute("productPage", productPage);

        return "sellers/seller";
    }
}
