package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "DTO для создания новой записи об образовании пользователя")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEducationDto {

    @NonNull
    @Schema(description = "Год начала обучения", example = "2015", required = true)
    private Integer yearFrom;

    @NonNull
    @Schema(description = "Год окончания обучения", example = "2019", required = true)
    private Integer yearTo;

    @NonNull
    @NotBlank(message = "It cannot be empty")
    @Schema(description = "Название учебного заведения",
            example = "Национальный исследовательский университет", required = true)
    private String institution;

    @NonNull
    @NotBlank(message = "It cannot be empty")
    @Schema(description = "Уровень образования", example = "Бакалавр", required = true)
    private String educationLevel;

    @NonNull
    @NotBlank(message = "It cannot be empty")
    @Schema(description = "Специализация или направление", example = "Информационные технологии", required = true)
    private String specialization;
}
