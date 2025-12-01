package uz.pdp.ecommerce;

import uz.pdp.ecommerce.entity.Customer;
import uz.pdp.ecommerce.service.CustomerService;
import uz.pdp.ecommerce.service.OrderService;
import uz.pdp.ecommerce.service.ProductService;
import uz.pdp.ecommerce.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ECommerceTelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CustomerService customerService;
    private final OrderService orderService;

    public ECommerceTelegramBot(ProductService productService,
                                CategoryService categoryService,
                                CustomerService customerService,
                                OrderService orderService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.customerService = customerService;
        this.orderService = orderService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();

            log.info("Received message: {} from user: {} (chatId: {})", messageText, userName, chatId);

            String response = handleCommand(messageText, userName, chatId);
            sendMessage(chatId, response, messageText.equals("/start"));
        }
    }

    private String handleCommand(String command, String userName, long chatId) {
        command = command.trim().toLowerCase();

        switch (command) {
            case "/start":
                registerCustomer(chatId, userName);
                return "🛒 *Welcome to E-Commerce Bot!*\n\n" +
                        "Hello, " + userName + "! 👋\n\n" +
                        "I can help you:\n" +
                        "🛍️ Browse products\n" +
                        "📦 Track orders\n" +
                        "👤 Manage your profile\n" +
                        "📊 View statistics\n\n" +
                        "Use the menu below to get started!";

            case "🛍️ products":
            case "/products":
                return getProductsList();

            case "📂 categories":
            case "/categories":
                return getCategoriesList();

            case "📦 my orders":
            case "/myorders":
                return getMyOrders(chatId);

            case "👤 profile":
            case "/profile":
                return getProfile(chatId);

            case "📊 statistics":
            case "/stats":
                return getStatistics();

            case "❓ help":
            case "/help":
                return """
                        📖 *Help Menu*
                        
                        Available commands:
                        
                        🛍️ Products - View all available products
                        📂 Categories - Browse product categories
                        📦 My Orders - Check your order history
                        👤 Profile - View your profile
                        📊 Statistics - System statistics
                        ❓ Help - This help menu
                        
                        For support: support@ecommerce.com""";

            default:
                if (command.startsWith("/product_")) {
                    Long productId = Long.parseLong(command.substring(9));
                    return getProductDetails(productId);
                }
                return "❌ Unknown command!\n\nUse the menu buttons or type /help for assistance.";
        }
    }

    private void registerCustomer(long chatId, String userName) {
        var existingCustomer = customerService.findByTelegramChatId(chatId);
        if (existingCustomer.isEmpty()) {
            Customer customer = new Customer();
            customer.setFirstName(userName);
            customer.setLastName("");
            customer.setEmail("telegram_" + chatId + "@temp.com");
            customer.setPhone("");
            customer.setTelegramChatId(chatId);
            customerService.save(customer);
            log.info("New customer registered: chatId={}, name={}", chatId, userName);
        }
    }

    private String getProductsList() {
        var products = productService.findActiveProducts();
        
        if (products.isEmpty()) {
            return "🛍️ *Products*\n\nNo products available at the moment.";
        }

        StringBuilder response = new StringBuilder("🛍️ *Available Products:*\n\n");
        for (var product : products) {
            response.append("▫️ *").append(product.getName()).append("*\n");
            response.append("   💰 Price: $").append(product.getPrice()).append("\n");
            response.append("   📦 Stock: ").append(product.getStockQuantity()).append(" units\n");
            response.append("   🏷️ Category: ").append(product.getCategoryName() != null ? product.getCategoryName() : "N/A").append("\n");
            response.append("   📝 ").append(product.getDescription() != null ? product.getDescription() : "No description").append("\n");
            response.append("   Use /product_").append(product.getId()).append(" for details\n\n");
        }
        
        response.append("Total products: ").append(products.size());
        return response.toString();
    }

    private String getProductDetails(Long productId) {
        return productService.findById(productId)
            .map(product -> {
                StringBuilder details = new StringBuilder();
                details.append("🛍️ *Product Details*\n\n");
                details.append("*Name:* ").append(product.getName()).append("\n");
                details.append("*SKU:* ").append(product.getSku()).append("\n");
                details.append("*Price:* $").append(product.getPrice()).append("\n");
                details.append("*Stock:* ").append(product.getStockQuantity()).append(" units\n");
                details.append("*Category:* ").append(product.getCategoryName() != null ? product.getCategoryName() : "N/A").append("\n\n");
                details.append("*Description:*\n").append(product.getDescription() != null ? product.getDescription() : "No description available");
                return details.toString();
            })
            .orElse("❌ Product not found!");
    }

    private String getCategoriesList() {
        var categories = categoryService.findAll();
        
        if (categories.isEmpty()) {
            return "📂 *Categories*\n\nNo categories available.";
        }

        StringBuilder response = new StringBuilder("📂 *Product Categories:*\n\n");
        for (var category : categories) {
            response.append("▫️ *").append(category.getName()).append("*\n");
            response.append("   Code: ").append(category.getCode()).append("\n");
            response.append("   ").append(category.getDescription() != null ? category.getDescription() : "No description").append("\n\n");
        }
        
        response.append("Total categories: ").append(categories.size());
        return response.toString();
    }

    private String getMyOrders(long chatId) {
        var customer = customerService.findByTelegramChatId(chatId);
        
        if (customer.isEmpty()) {
            return "❌ Customer profile not found. Please use /start first.";
        }

        var orders = orderService.findByCustomerId(customer.get().getId());
        
        if (orders.isEmpty()) {
            return "📦 *My Orders*\n\nYou haven't placed any orders yet.";
        }

        StringBuilder response = new StringBuilder("📦 *Your Orders:*\n\n");
        for (var order : orders) {
            response.append("▫️ Order #").append(order.getOrderNumber()).append("\n");
            response.append("   💰 Total: $").append(order.getTotalAmount()).append("\n");
            response.append("   📊 Status: ").append(order.getStatus()).append("\n");
            response.append("   📅 Date: ").append(order.getOrderDate()).append("\n\n");
        }
        
        response.append("Total orders: ").append(orders.size());
        return response.toString();
    }

    private String getProfile(long chatId) {
        return customerService.findByTelegramChatId(chatId)
            .map(customer -> {
                StringBuilder profile = new StringBuilder();
                profile.append("👤 *Your Profile*\n\n");
                profile.append("*Name:* ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append("\n");
                profile.append("*Email:* ").append(customer.getEmail()).append("\n");
                profile.append("*Phone:* ").append(customer.getPhone() != null && !customer.getPhone().isEmpty() ? customer.getPhone() : "Not set").append("\n");
                profile.append("*Address:* ").append(customer.getAddress() != null ? customer.getAddress() : "Not set").append("\n");
                profile.append("*City:* ").append(customer.getCity() != null ? customer.getCity() : "Not set").append("\n");
                profile.append("*Member since:* ").append(customer.getCreatedAt()).append("\n");
                return profile.toString();
            })
            .orElse("❌ Profile not found. Please use /start first.");
    }

    private String getStatistics() {
        long productCount = productService.count();
        long categoryCount = categoryService.count();
        long customerCount = customerService.count();
        long orderCount = orderService.count();

        return "📊 *System Statistics*\n\n" +
                "🛍️ Products: " + productCount + "\n" +
                "📂 Categories: " + categoryCount + "\n" +
                "👥 Customers: " + customerCount + "\n" +
                "📦 Orders: " + orderCount + "\n\n" +
                "✅ System is running smoothly!";
    }

    private void sendMessage(long chatId, String text, boolean withKeyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.enableMarkdown(true);

        if (withKeyboard) {
            message.setReplyMarkup(createMainKeyboard());
        }

        try {
            execute(message);
            log.info("Message sent to chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Error sending message to chatId: {}", chatId, e);
        }
    }

    private ReplyKeyboardMarkup createMainKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("🛍️ Products"));
        row1.add(new KeyboardButton("📂 Categories"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("📦 My Orders"));
        row2.add(new KeyboardButton("👤 Profile"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("📊 Statistics"));
        row3.add(new KeyboardButton("❓ Help"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}