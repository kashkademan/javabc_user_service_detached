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
import java.util.List;

/**
 * DTO для создания нового пользовательского события.
 * <p>
 * Используется для передачи данных в теле запроса при создании события.
 * Содержит обязательные поля: название, описание, дата начала и окончания, тип события.
 * </p>
 *
 * @author JekaCAP
 * @see school.faang.user_service.entity.event.Event
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateDto {

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

    private List<Long> attendeesIds;

    @NotBlank(message = "Location must not be blank")
    private String location;

    @NotNull(message = "Event status must be provided")
    private EventStatus status;
}
