package uz.pdp.ecommerce.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(uz.pdp.ecommerce.config.ECommerceApplication.class, args);
        System.out.println("\n" +
                "╔═══════════════════════════════════════════════════════════╗\n" +
                "║                                                           ║\n" +
                "║           🛒 E-COMMERCE SYSTEM STARTED 🛒                ║\n" +
                "║                                                           ║\n" +
                "║  Web Interface: http://localhost:8080                    ║\n" +
                "║  Telegram Bot: Active and ready!                         ║\n" +
                "║                                                           ║\n" +
                "╚═══════════════════════════════════════════════════════════╝\n");
    }
}
