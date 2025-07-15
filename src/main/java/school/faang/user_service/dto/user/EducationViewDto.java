package school.faang.user_service.dto.user;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationViewDto {
    private long id;
    private Integer yearFrom;
    private Integer yearTo;
    private String institution;
    private String educationLevel;
    private String specialization;
}
