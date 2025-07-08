package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEducationDto {

    @NonNull
    @NotBlank(message = "It cannot be empty")
    private Integer yearFrom;

    @NonNull
    @NotBlank(message = "It cannot be empty")
    private Integer yearTo;

    @NonNull
    @NotBlank(message = "It cannot be empty")
    private String institution;

    @NonNull
    @NotBlank(message = "It cannot be empty")
    private String educationLevel;

    @NonNull
    @NotBlank(message = "It cannot be empty")
    private String specialization;
}
