package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO для передачи причины отказа (отклонения) запроса.
 * <p>
 * Используется во всех случаях, когда необходимо указать
 * текстовую причину отказа или отклонения действия.
 * <p>
 *
 * @author JekaCAP
 */
public record RejectionDto(
        @NotBlank String reason
) {
}