package uz.pdp.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.pdp.ecommerce.entity.Product;
import uz.pdp.ecommerce.service.CategoryService;
import uz.pdp.ecommerce.service.ProductService;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("activeTab", "products");
        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("product", new Product());
        model.addAttribute("productCount", productService.count());
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return productService.findById(id)
            .map(product -> {
                model.addAttribute("activeTab", "products");
                model.addAttribute("products", productService.findAll());
                model.addAttribute("categories", categoryService.findAll());
                model.addAttribute("product", product);
                model.addAttribute("productCount", productService.count());
                return "index";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
                return "redirect:/products";
            });
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        try {
            productService.save(product);
            redirectAttributes.addFlashAttribute("successMessage", 
                product.getId() == null ? "Product added successfully!" : "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }
}
