package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.ecommerce.data.domain.EternalProduct;
import pl.ecommerce.web.service.ArchiveService;

@Controller
@RequestMapping("/products/archive")
@AllArgsConstructor
public class ArchiveController {

    private final ArchiveService archiveService;


    @GetMapping("{id}")
    public String showArchivalProduct(@PathVariable Long id, Model model) {

        EternalProduct eternalProduct = archiveService.getEternalProduct(id);

        model.addAttribute("product", eternalProduct);

        return "archive/show";
    }
}
