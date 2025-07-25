package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи причины отказа (отклонения) запроса.
 * <p>
 * Используется во всех случаях, когда необходимо указать
 * текстовую причину отказа или отклонения действия.
 * <p>
 *
 * @author JekaCAP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectionDto {

    @NotBlank(message = "Rejection reason must not be blank")
    private String reason;
}