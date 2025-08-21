package school.faang.user_service.telegram;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramCommand {
    void handle(Long chatId, String payload) throws TelegramApiException;
}