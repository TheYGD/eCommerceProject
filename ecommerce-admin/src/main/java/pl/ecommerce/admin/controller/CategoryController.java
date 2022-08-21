package pl.ecommerce.admin.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.ecommerce.admin.service.CategoryService;
import pl.ecommerce.data.dto.CategoryDto;
import pl.ecommerce.data.domain.Category;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
@AllArgsConstructor
public class CategoryController {

    private CategoryService categoryService;


    @GetMapping("")
    public String listCategories(Model model) {
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);

        return "category/list";
    }

    @GetMapping("/new")
    public String addCategory(@ModelAttribute("category") CategoryDto categoryDTO) {
        return "category/add";
    }

    @PostMapping("/new")
    public String postCategory(@ModelAttribute("category") CategoryDto categoryDTO) {
        categoryService.addCategory(categoryDTO);

        return "redirect:/admin/categories";
    }
}
