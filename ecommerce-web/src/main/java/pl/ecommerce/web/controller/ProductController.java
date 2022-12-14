package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.other.StringResponse;
import pl.ecommerce.web.service.ProductService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private ProductService productService;



    @GetMapping("/{id}")
    public String showById(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);

        // archive
        if (product.getAvailableProduct() == null) {
            model.addAttribute("product", product);
            return "archive/show";
        }

        model.addAttribute("availableProduct", product.getAvailableProduct());
        return "products/show";
    }


    @GetMapping()
    public String showProducts(@RequestParam(value = "category", defaultValue = "1") Long categoryId,
                               @RequestParam(value = "search", defaultValue = "") String query,
                               Model model, @RequestParam(defaultValue = "1") int pageNr,
                               @RequestParam(defaultValue = "0") int sortOption,
                               @RequestParam( required = false ) String price,
                               @RequestParam Map<String,
                               String> otherValues) {

        Category category = productService.getCategory(categoryId);
        Page<AvailableProduct> productPage = productService.findProducts(category, query, pageNr, sortOption, price,
                otherValues, null);
        SortOption[] sortOptions = SortOption.values();

        model.addAttribute("category", category);
        model.addAttribute("productPage", productPage);
        model.addAttribute("sortOptions", sortOptions);

        return "products/list";
    }


    @PostMapping("/add/{id}")
    @ResponseBody
    public StringResponse addProductToCart(@AuthenticationPrincipal UserCredentials userCredentials,
                                           @PathVariable("id") Long productId, @RequestParam Integer quantity,
                                           HttpServletRequest request, HttpServletResponse response) {

        productService.addProductToCart(userCredentials, productId, quantity, request, response);
        return new StringResponse("Product added to cart.");
    }


    @GetMapping("/{id}/ask")
    public String askAboutProductPage(@PathVariable("id") Long productId, Model model) {

        List<MessageCause> messageCauseList = productService.getProductMessageTitles();
        AvailableProduct availableProduct = productService.findById(productId).getAvailableProduct();

        model.addAttribute("messageCauseList", messageCauseList);
        model.addAttribute("availableProduct", availableProduct);

        return "messages/create-product";
    }

    @PostMapping("/{id}/ask")
    public String askAboutProduct(@AuthenticationPrincipal UserCredentials userCredentials,
                                  @PathVariable("id") Long productId,
                                  @RequestParam Map<String, String> formData) {

        Long chatId = productService.askAboutProduct(userCredentials, productId, formData);

        return "redirect:/messages?id=" + chatId;
    }
}
