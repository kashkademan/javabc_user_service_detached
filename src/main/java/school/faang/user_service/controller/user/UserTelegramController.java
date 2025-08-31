package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.user.UserService;

/**
 * Контроллер для работы с привязкой Telegram аккаунта пользователя.
 * <p>
 * Предоставляет эндпоинт для генерации ссылки, по которой пользователь
 * сможет открыть бота Telegram и автоматически передать свой userId
 * для последующей привязки chatId.
 * </p>
 * <p>
 * Использует {@link UserContext} для определения текущего пользователя
 * на основе заголовка запроса и {@link UserService} для генерации ссылки.
 * </p>
 *
 * @author agent
 * @since 15.08.2025
 */
@RestController
@RequestMapping("/users/telegram")
@RequiredArgsConstructor
public class UserTelegramController {

    private final UserService userService;
    private final UserContext context;

    @GetMapping("/connect")
    public ResponseEntity<String> getBotLink() {
        var userId = context.getUserId();
        String link = userService.generateTelegramLink(userId);
        return ResponseEntity.ok(link);
    }
}