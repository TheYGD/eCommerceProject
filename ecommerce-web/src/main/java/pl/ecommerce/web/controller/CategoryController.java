package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.ecommerce.data.dto.CategoryAttributeDto;
import pl.ecommerce.web.service.CategoryService;

import java.util.List;

@Controller
@RequestMapping("/categories/")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping("/{id}/attributes")
    @ResponseBody
    public List<CategoryAttributeDto> getCategoryAttributes(@PathVariable Long id) {

        return categoryService.getCategoryAttributes(id);
    }
}
