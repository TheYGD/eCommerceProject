package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.data.dto.ProductDto;
import pl.ecommerce.data.entity.Category;
import pl.ecommerce.data.entity.UserCredentials;
import pl.ecommerce.web.service.AddProductService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/add-product")
@AllArgsConstructor
public class AddProductController {

    private final AddProductService addProductService;


    @GetMapping
    public String addProductPage(Model model, ProductDto productDto) {
        List<Category> categoryList = addProductService.getCategoryList();

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("productDto", productDto);

        return "add-product/show";
    }

    @PostMapping
    public String postProduct(@ModelAttribute @Valid ProductDto productDto,
                              @AuthenticationPrincipal UserCredentials userCredentials) {
        Long id = addProductService.createProduct(productDto, userCredentials);

        return "redirect:/products/" + id;
    }
}
