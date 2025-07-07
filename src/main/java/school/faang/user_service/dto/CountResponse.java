package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * CountResponse — описание класса.
 * <p>
 * Универсальный класс для подсчёта, участников, подписчиков.
 * </p>
 *
 * @author agent
 * @since 05.07.2025
 */
@Data
@AllArgsConstructor
public class CountResponse {
    private long count;
}