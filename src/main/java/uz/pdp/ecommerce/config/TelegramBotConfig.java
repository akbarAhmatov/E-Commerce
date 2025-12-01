// DatabaseInitializer.java
package uz.pdp.ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.ecommerce.ECommerceTelegramBot;

@Slf4j
@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(ECommerceTelegramBot eCommerceTelegramBot) {
        TelegramBotsApi botsApi = null;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(eCommerceTelegramBot);
            log.info("✅ Telegram Bot registered successfully: @{}", eCommerceTelegramBot.getBotUsername());
        } catch (TelegramApiException e) {
            log.error("❌ Failed to register Telegram Bot", e);
        }
        return botsApi;
    }
}