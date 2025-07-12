package school.faang.user_service.dto.education;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * CreateEducationDto — DTO для добавления нового образования пользователя.
 * <p>
 * Используется для передачи данных в теле запроса при создании нового образования.
 * Содержит обязательные поля: год поступление, год окончания, институт, уровень образование, специальность
 * </p>
 *
 * @author fomchenkoandrey
 * @since 04.07.2025
 */

public record UpdateEducationDto(
        @NotNull
        @NotBlank(message = "It cannot be empty")
        Integer yearFrom,

        @NotNull
        @NotBlank(message = "It cannot be empty")
        Integer yearTo,

        @NotNull
        @NotBlank(message = "It cannot be empty")
        String institution,

        @NotNull
        @NotBlank(message = "It cannot be empty")
        String educationLevel,

        @NotNull
        @NotBlank(message = "It cannot be empty")
        String specialization
) {
}
