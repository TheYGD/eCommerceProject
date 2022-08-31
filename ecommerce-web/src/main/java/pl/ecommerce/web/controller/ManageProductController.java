package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.data.domain.AvailableProduct;
import pl.ecommerce.data.dto.ProductDto;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.data.other.StringResponse;
import pl.ecommerce.web.service.ManageProductService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/products")
@AllArgsConstructor
public class ManageProductController {

    private final ManageProductService manageProductService;


    @GetMapping("/create")
    public String addProductPage(Model model) {
        List<Category> categoryList = manageProductService.getCategoryList();

        model.addAttribute("product", new ProductDto());
        model.addAttribute("categoryList", categoryList);

        return "manage-product/show";
    }


    @PostMapping("/create")
    public String postProduct(@AuthenticationPrincipal UserCredentials userCredentials,
                              @ModelAttribute @Valid ProductDto productDto) {
        Long id = manageProductService.createProduct(userCredentials, productDto);

        return "redirect:/products/" + id;
    }


    @GetMapping("/edit/{id}")
    public String editProductPage(@AuthenticationPrincipal UserCredentials userCredentials, Model model,
                                    @PathVariable Long id) {
        List<Category> categoryList = manageProductService.getCategoryList();
        AvailableProduct availableProduct = manageProductService.getProduct(userCredentials, id);
        ProductDto productDto = manageProductService.getProductDto(userCredentials, availableProduct);

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("product", productDto);
        model.addAttribute("productId", availableProduct.getId());
        model.addAttribute("image", availableProduct.getProduct().getImage());

        return "manage-product/show";
    }


    @PostMapping("/edit/{id}")
    public String editProduct(@AuthenticationPrincipal UserCredentials userCredentials,
                              @ModelAttribute @Valid ProductDto productDto, @PathVariable Long id) {
        manageProductService.editProduct(userCredentials, productDto, id);

        return "redirect:/products/" + id;
    }


    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteProduct(@AuthenticationPrincipal UserCredentials userCredentials, @PathVariable Long id) {
        manageProductService.deleteProduct(userCredentials, id);

        return ResponseEntity.status(HttpStatus.OK).body("Product deleted");
    }
}
