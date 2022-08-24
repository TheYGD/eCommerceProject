package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.domain.Product;
import pl.ecommerce.web.service.CategoryService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping("/{id}")
    public String showCategory(@PathVariable Long id, Model model, @RequestParam(defaultValue = "1") int pageNr,
                               @RequestParam(defaultValue = "0") int sortOption) {

        Category category = categoryService.getCategory(id);
        Page<Product> productPage = categoryService.getProductList(category, pageNr, sortOption);
        model.addAttribute("category", category);
        model.addAttribute("productPage", productPage);

        return "categories/show";
    }
}
