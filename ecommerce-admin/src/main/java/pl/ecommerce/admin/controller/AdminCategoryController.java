package pl.ecommerce.admin.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.admin.service.AdminCategoryService;
import pl.ecommerce.data.domain.ProductAttribute;
import pl.ecommerce.data.dto.CategoryDto;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.dto.PseudoEnumDto;
import pl.ecommerce.data.other.StringResponse;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/categories")
@AllArgsConstructor
public class AdminCategoryController {

    private AdminCategoryService categoryService;


    @GetMapping("")
    public String listCategories(Model model) {
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);

        return "categories/list";
    }


    @GetMapping("/new")
    public String addCategory(@ModelAttribute("category") CategoryDto categoryDTO, Model model) {

        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);

        return "categories/add";
    }

    @PostMapping("/new")
    public String postCategory(@ModelAttribute("category") @Valid CategoryDto categoryDTO) {
        categoryService.addCategory(categoryDTO);

        return "redirect:/admin/categories";
    }


    @GetMapping("/{id}")
    public String showCategory(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        model.addAttribute("attribute", new ProductAttribute());

        return "categories/show";
    }

    @PostMapping("/attributes/{categoryId}/new")
    @ResponseBody
    public StringResponse postAttribute(@PathVariable Long categoryId, @ModelAttribute @Valid ProductAttribute attribute) {

        categoryService.postAttribute(categoryId, attribute);

        return new StringResponse("Attribute created!");
    }

    @PostMapping("/attributes/new")
    @ResponseBody
    public Long createEnumObject(@RequestBody @Valid PseudoEnumDto pseudoEnumDto) {

        Long createdId = categoryService.createEnumObject(pseudoEnumDto);

        return createdId;
    }


    @DeleteMapping("/attributes/{id}/delete")
    @ResponseBody
    public StringResponse deleteAttribute(@PathVariable Long id) {

        categoryService.deleteAttribute(id);

        return new StringResponse("Attribute deleted");
    }
}
