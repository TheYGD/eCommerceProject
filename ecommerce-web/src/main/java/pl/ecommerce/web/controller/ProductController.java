package pl.ecommerce.ecommerceweb.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.ecommerce.ecommercedomain.Product;
import pl.ecommerce.ecommerceservice.ProductService;

@Controller
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @GetMapping("/{id}")
    public String showById(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);

        return "product/show";
    }
}