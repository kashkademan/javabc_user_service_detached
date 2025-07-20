package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO для фильтрации пользователей при поиске.
 * <p>
 * Содержит параметры фильтрации пользователей по имени,
 * электронной почты, номеру телефона и информации "о себе".
 * </p>*
 *
 * @param usernameContains подстрока имени пользователя
 * @param emailContains подстрока электронной почты
 * @param phone номер телефона
 * @param aboutMeContains подстрока в информации о себе
 * @author Myrza
 * @since 16.07.2025
 */
public record UserFilterDto(
        @Size(max = 64)
        String usernameContains,
        @Size(max = 64)
        String emailContains,
        @Size(max = 32)
        String phone,
        @Size(max = 4096)
        String aboutMeContains,
        @NotNull
        Boolean onlyPremium
) {
}
