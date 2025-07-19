package school.faang.user_service.rating_service.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO, представляющий пользователя и его рейтинг в таблице лидеров.
 * <p>
 * Используется для отображения информации о пользователях с наивысшими баллами в системе.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderDto {
    private Long userId;
    private String userName;
    private Long score;
}