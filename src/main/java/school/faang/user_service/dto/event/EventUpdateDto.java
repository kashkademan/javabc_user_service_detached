package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

/**
 * DTO для обновления существующего события.
 * <p>
 * Содержит поля, необходимые для изменения данных события.
 * Валидация обеспечивает наличие обязательных полей и корректность дат.
 * </p>
 * <p>
 * Обратите внимание, что startDate и endDate должны быть в будущем,
 * а также желательно, чтобы endDate была позже startDate (валидация на уровне сервиса).
 * </p>
 *
 * @see school.faang.user_service.entity.event.Event
 * @author JekaCAP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateDto {

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Start date must be provided")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date must be provided")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @NotNull(message = "Event type must be provided")
    private EventType type;

    @NotNull(message = "Event status must be provided")
    private EventStatus status;

    @NotBlank(message = "Location must not be blank")
    private String location;
}
