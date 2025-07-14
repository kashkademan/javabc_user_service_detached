package school.faang.user_service.dto.education;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "EducationDTO", description = "Данные об образовании")
public class EducationDto {
    @Schema(name = "yearFrom", description = "Год начала обучения", example = "2000", required = true)
    @NotNull
    @Positive(message = "Год начала обучения должен быть больше 0")
    private Integer yearFrom;
    @Schema(name = "yearTo", description = "Год окончанияа обучения", example = "2005", required = true)
    @NotNull
    @Positive
    private Integer yearTo;
    @NotNull
    @Positive
    private String institution;
    @Schema(name = "educationLevel", description = "Уровень образования", required = true)
    private String educationLevel;
    @Schema(name = "specializatio", description = "Специализация", required = true)
    private String specialization;
}
