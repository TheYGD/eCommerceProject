package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.data.entity.Product;
import pl.ecommerce.web.service.ProductService;

import java.util.List;

@Controller
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @GetMapping("/{id}")
    public String showById(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);

        return "products/show";
    }

    @GetMapping()
    public String showAll(Model model) {
        List<Product> productList = productService.findAll();
        model.addAttribute("productList", productList);

        return "products/list";
    }

    @PostMapping()
    public String findByQuery(@RequestParam("search") String query, Model model) {
        List<Product> productList = productService.findByQuery(query);
        model.addAttribute("productList", productList);
        model.addAttribute("searchQuery", query);

        return "products/list";
    }
}
