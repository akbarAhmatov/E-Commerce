package uz.pdp.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.pdp.ecommerce.entity.Order;
import uz.pdp.ecommerce.service.CustomerService;
import uz.pdp.ecommerce.service.OrderService;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CustomerService customerService;

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("activeTab", "orders");
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("order", new Order());
        model.addAttribute("orderCount", orderService.count());
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return orderService.findById(id)
            .map(order -> {
                model.addAttribute("activeTab", "orders");
                model.addAttribute("orders", orderService.findAll());
                model.addAttribute("customers", customerService.findAll());
                model.addAttribute("order", order);
                model.addAttribute("orderCount", orderService.count());
                return "index";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Order not found!");
                return "redirect:/orders";
            });
    }

    @PostMapping("/save")
    public String saveOrder(@ModelAttribute Order order, RedirectAttributes redirectAttributes) {
        try {
            orderService.save(order);
            redirectAttributes.addFlashAttribute("successMessage", 
                order.getId() == null ? "Order created successfully!" : "Order updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving order: " + e.getMessage());
        }
        return "redirect:/orders";
    }

    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Order deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting order: " + e.getMessage());
        }
        return "redirect:/orders";
    }
}
