package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO с информацией о пользователе")
public record UserDto(

        @Schema(description = "Идентификатор пользователя", example = "123")
        Long id,

        @Schema(description = "Имя пользователя", example = "john_doe")
        String username,

        @Schema(description = "Электронная почта пользователя", example = "john@example.com")
        String email,

        @Schema(description = "Номер телефона пользователя", example = "+77771234567")
        String phone,

        @Schema(description = "Информация о себе", example = "Java developer, люблю открытый исходный код.")
        String aboutMe,

        @Schema(description = "URL аватарки пользователя", example = "https://example.com/avatars/john.png")
        String avatarUrl
) {
}