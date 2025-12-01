package uz.pdp.ecommerce.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseInitializer {

    @EventListener(ApplicationReadyEvent.class)
    public void createDatabaseIfNotExists() {
        String postgresUrl = "jdbc:postgresql://localhost:5432/ecommerce_db";
        String username = "postgres";
        String password = "akbar_123456789"; // Change this
        String dbName = "ecommerce_db";

        try (Connection connection = DriverManager.getConnection(postgresUrl, username, password);
             Statement statement = connection.createStatement()) {

            String checkDbQuery = "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'";
            var resultSet = statement.executeQuery(checkDbQuery);

            if (!resultSet.next()) {
                String createDbQuery = "CREATE DATABASE " + dbName;
                statement.executeUpdate(createDbQuery);
                System.out.println("✅ Database '" + dbName + "' created successfully!");
            } else {
                System.out.println("✅ Database '" + dbName + "' already exists.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error creating database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
