package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO для создания нового пользователя")
public record UserCreateDto(

        @NotBlank
        @Schema(description = "Имя пользователя", example = "john_doe")
        String username,

        @NotBlank
        @Schema(description = "Электронная почта пользователя", example = "john@example.com")
        String email,

        @NotBlank
        @Schema(description = "Пароль пользователя", example = "securePassword123")
        String password,

        @NotNull
        @Schema(description = "Идентификатор страны", example = "1")
        Long countryId,

        @NotBlank
        @Schema(description = "Тип уведомления")
        String contact
) {
}