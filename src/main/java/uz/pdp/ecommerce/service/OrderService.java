package uz.pdp.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ecommerce.entity.Order;
import uz.pdp.ecommerce.entity.OrderItem;
import uz.pdp.ecommerce.repository.OrderItemRepository;
import uz.pdp.ecommerce.repository.OrderRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    public List<Order> findAll() {
        List<Order> orders = orderRepository.findAll();
        orders.forEach(this::enrichOrder);
        return orders;
    }

    public Optional<Order> findById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        order.ifPresent(this::enrichOrder);
        return order;
    }

    public List<Order> findByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        orders.forEach(this::enrichOrder);
        return orders;
    }

    public List<Order> findByStatus(Order.OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        orders.forEach(this::enrichOrder);
        return orders;
    }

    @Transactional
    public Order save(Order order) {
        if (order.getOrderNumber() == null) {
            order.setOrderNumber(generateOrderNumber());
        }
        return orderRepository.save(order);
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    public long count() {
        return orderRepository.count();
    }

    public List<OrderItem> findOrderItems(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        items.forEach(item -> {
            productService.findById(item.getProductId())
                .ifPresent(product -> item.setProductName(product.getName()));
        });
        return items;
    }

    private void enrichOrder(Order order) {
        if (order.getCustomerId() != null) {
            customerService.findById(order.getCustomerId())
                .ifPresent(customer -> 
                    order.setCustomerName(customer.getFirstName() + " " + customer.getLastName()));
        }
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD-" + timestamp;
    }
}
