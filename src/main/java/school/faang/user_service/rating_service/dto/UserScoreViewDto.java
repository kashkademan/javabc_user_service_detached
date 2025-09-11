package school.faang.user_service.rating_service.dto;

/**
 * DTO для представления данных о баллах пользователя
 *
 * @param userId ID пользователя
 * @param score его баллы
 *
 * @author Linempy
 * @since 11.09.2025
 */
public record UserScoreViewDto(Long userId, Double score) {
}