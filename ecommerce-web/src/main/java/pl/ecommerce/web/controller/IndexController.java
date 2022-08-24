package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.ecommerce.data.domain.Category;
import pl.ecommerce.web.service.IndexService;

import java.util.List;

@Controller
@AllArgsConstructor
public class IndexController {

    private final IndexService indexService;


    @GetMapping()
    public String indexPage(Model model) {

        List<Category> categoryList = indexService.getAllCategories();
        categoryList.addAll(categoryList);
        categoryList.addAll(categoryList);
        model.addAttribute("categoryList", categoryList);

        return "index";
    }
}
