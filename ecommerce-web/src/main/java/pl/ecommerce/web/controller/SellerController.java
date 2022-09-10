package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.ecommerce.data.domain.AvailableProduct;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.domain.User;
import pl.ecommerce.web.service.SellerService;

import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService sellerService;


    @GetMapping("/{sellerId}")
    public String sellersProfile(@PathVariable Long sellerId,
                                 @RequestParam(value = "category", defaultValue = "1") Long categoryId,
                                 @RequestParam(value = "search", defaultValue = "") String query,
                                 Model model, @RequestParam(defaultValue = "1") int pageNr,
                                 @RequestParam(defaultValue = "0") int sortOption,
                                 @RequestParam(required = false) String price,
                                 @RequestParam Map<String, String> otherValues) {

        User seller = sellerService.getSeller(sellerId);
        Category category = sellerService.getCategory(categoryId);
        Page<AvailableProduct> productPage = sellerService.findProducts(category, query, pageNr, sortOption, price,
                otherValues, sellerId);

        model.addAttribute("seller", seller);
        model.addAttribute("category", category);
        model.addAttribute("productPage", productPage);

        return "sellers/show";
    }
}
