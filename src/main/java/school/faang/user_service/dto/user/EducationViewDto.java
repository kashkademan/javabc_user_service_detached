package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EducationDto — содержит даные об образовании пользователя.
 * <p>
 * Содержит все основные поля образования необходимые для вывода клиента.
 * Содержит обязательные поля: год поступление, год окончания, институт, уровень образование, специальность
 * </p>*
 *
 * @author fomchenkoandrey
 * @since 04.07.2025
 */

@Schema(description = "DTO для отображения информации об образовании пользователя")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationViewDto {

    @Schema(description = "Идентификатор записи об образовании", example = "42")
    private long id;

    @Schema(description = "Год начала обучения", example = "2015")
    private Integer yearFrom;

    @Schema(description = "Год окончания обучения", example = "2019")
    private Integer yearTo;

    @Schema(description = "Название учебного заведения", example = "Национальный университет")
    private String institution;

    @Schema(description = "Уровень образования", example = "Бакалавр")
    private String educationLevel;

    @Schema(description = "Специализация / направление", example = "Прикладная информатика")
    private String specialization;
}