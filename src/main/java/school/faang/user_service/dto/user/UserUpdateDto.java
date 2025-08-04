package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO для обновления пользователя")
public record UserUpdateDto(

        @NotBlank
        @Schema(description = "Имя пользователя", example = "john_doe")
        String username,

        @NotBlank
        @Schema(description = "Email пользователя", example = "john@example.com")
        String email,

        @Schema(description = "Телефон пользователя", example = "+1234567890")
        String phone,

        @Schema(description = "Информация о себе", example = "Программист, геймер, люблю кофе.")
        String aboutMe,

        @NotNull
        @Schema(description = "ID страны", example = "1")
        Long countryId,

        @Schema(description = "Название города", example = "Алматы")
        String city
) {
}