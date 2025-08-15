package school.faang.user_service.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.user.UserService;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBotChatId extends TelegramLongPollingBot {

    private final UserService userService;
    private final UserContext context;

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
                try {
                    String userIdStr = text.replace("/start", "").trim();
                    Long userId = Long.parseLong(userIdStr);

                    userService.linkChatId(userId, chatId);

                    SendMessage sendingMessage = new SendMessage();

                    sendingMessage.setChatId(chatId);
                    sendingMessage.setText("Ваш Telegram успешно привязан к аккаунту!");

                    execute(sendingMessage);

                } catch (TelegramApiException e) {

                    log.error("Ошибка при сохранении chatId", e);
                }
            }
        }
    }
}
