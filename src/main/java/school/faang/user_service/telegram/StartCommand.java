package school.faang.user_service.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartCommand implements TelegramCommand {

    private final UserService userService;
    private final TelegramLongPollingBot bot;

    @Async
    @Override
    public void handle(Long chatId, String payload) {
        try {
            Long userId = Long.parseLong(payload.trim());
            userService.linkChatId(userId, chatId);

            sendMessage(chatId, "Ваш Telegram успешно привязан к аккаунту!");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Некорректный формат userId!");
        }
    }

    private void sendMessage(Long chatId, String text) {
        try {
            SendMessage sendMessage = new SendMessage(chatId.toString(), text);
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения", e);
        }
    }
}