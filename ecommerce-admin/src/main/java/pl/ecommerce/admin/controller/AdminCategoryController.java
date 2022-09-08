package pl.ecommerce.admin.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.admin.service.AdminCategoryService;
import pl.ecommerce.data.domain.CategoryAttribute;
import pl.ecommerce.data.dto.CategoryDto;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.dto.PseudoEnumDto;
import pl.ecommerce.data.other.StringResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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
        model.addAttribute("attribute", new CategoryAttribute());

        return "categories/show";
    }

    @PostMapping("/attributes/{categoryId}/new")
    @ResponseBody
    public StringResponse postAttribute(@PathVariable Long categoryId, @ModelAttribute @Valid CategoryAttribute attribute,
                                        @RequestParam("enumValue") List<String> enumValues) {

        categoryService.postAttribute(categoryId, attribute, enumValues);

        return new StringResponse("Attribute created!");
    }


    @DeleteMapping("/attributes/{id}/delete")
    @ResponseBody
    public StringResponse deleteAttribute(@PathVariable Long id) {

        categoryService.deleteAttribute(id);

        return new StringResponse("Attribute deleted");
    }
}
