package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
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
 * @param emailContains    подстрока электронной почты
 * @param phone            номер телефона
 * @param aboutMeContains  подстрока в информации о себе
 * @author Myrza
 * @since 16.07.2025
 */
@Schema(description = "DTO для фильтрации пользователей при поиске")
public record UserFilterDto(

        @Size(max = 64)
        @Schema(
                description = "Подстрока имени пользователя для поиска",
                example = "john"
        )
        String usernameContains,

        @Size(max = 64)
        @Schema(
                description = "Подстрока email пользователя для поиска",
                example = "@gmail.com"
        )
        String emailContains,

        @Size(max = 32)
        @Schema(
                description = "Телефон пользователя",
                example = "+77771234567"
        )
        String phone,

        @Size(max = 4096)
        @Schema(
                description = "Подстрока информации 'о себе'",
                example = "Java developer"
        )
        String aboutMeContains,

        @NotNull
        @Schema(
                description = "Флаг: искать только премиум-пользователей",
                example = "true",
                required = true
        )
        Boolean onlyPremium
) {
}