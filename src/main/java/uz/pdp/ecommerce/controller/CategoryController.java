package uz.pdp.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.pdp.ecommerce.entity.Category;
import uz.pdp.ecommerce.service.CategoryService;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("activeTab", "categories");
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("category", new Category());
        model.addAttribute("categoryCount", categoryService.count());
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return categoryService.findById(id)
            .map(category -> {
                model.addAttribute("activeTab", "categories");
                model.addAttribute("categories", categoryService.findAll());
                model.addAttribute("category", category);
                model.addAttribute("categoryCount", categoryService.count());
                return "index";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Category not found!");
                return "redirect:/categories";
            });
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("successMessage", 
                category.getId() == null ? "Category added successfully!" : "Category updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving category: " + e.getMessage());
        }
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting category: " + e.getMessage());
        }
        return "redirect:/categories";
    }
}
