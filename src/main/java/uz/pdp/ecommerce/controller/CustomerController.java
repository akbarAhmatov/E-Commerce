package uz.pdp.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.pdp.ecommerce.entity.Customer;
import uz.pdp.ecommerce.service.CustomerService;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("activeTab", "customers");
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("customer", new Customer());
        model.addAttribute("customerCount", customerService.count());
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return customerService.findById(id)
            .map(customer -> {
                model.addAttribute("activeTab", "customers");
                model.addAttribute("customers", customerService.findAll());
                model.addAttribute("customer", customer);
                model.addAttribute("customerCount", customerService.count());
                return "index";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Customer not found!");
                return "redirect:/customers";
            });
    }

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        try {
            customerService.save(customer);
            redirectAttributes.addFlashAttribute("successMessage", 
                customer.getId() == null ? "Customer added successfully!" : "Customer updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving customer: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Customer deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting customer: " + e.getMessage());
        }
        return "redirect:/customers";
    }
}
