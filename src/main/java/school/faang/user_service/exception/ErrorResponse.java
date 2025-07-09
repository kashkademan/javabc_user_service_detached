package school.faang.user_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * ErrorResponse — описание класса.
 * <p>
 * Класс для возврата из обработчиков ошибок
 * </p>
 *
 * @author agent
 * @since 05.07.2025
 */
@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String message;
    private final String timestamp;
}