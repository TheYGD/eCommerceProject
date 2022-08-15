package pl.ecommerce.admin.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.ecommerce.admin.service.CategoryService;
import pl.ecommerce.domain.dto.CategoryDto;

@Controller
@RequestMapping("/admin/categories")
@AllArgsConstructor
public class CategoryController {

    private CategoryService categoryService;

    @GetMapping("/new")
    public String addCategory(@ModelAttribute CategoryDto categoryDTO) {
        return "category/add";
    }

    @PostMapping("/new")
    public String postCategory(@ModelAttribute CategoryDto categoryDTO) {

        return "redirect:/";
    }

    @GetMapping("")
    public String addCategor2y(@ModelAttribute CategoryDto categoryDTO) {
        return "category/add";
    }
}
