package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationViewDto {

    @NonNull
    @NotBlank(message = "It cannot be empty")
    private long id;

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
