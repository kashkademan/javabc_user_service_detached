package school.faang.user_service.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBotChatId extends TelegramLongPollingBot {

    private final StartCommand startCommand;

    @Value("${telegram.bot-username}")
    private String botUsername;

    @Value("${telegram.bot-token}")
    private String botToken;

    private SendMessage message;

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
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();

            if (text.startsWith("/start")) {
                String payload = text.replace("/start", "").trim();
                startCommand.handle(chatId, payload);
            }
        }
    }
}