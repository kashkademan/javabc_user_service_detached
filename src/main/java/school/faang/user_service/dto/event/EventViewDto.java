package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

/**
 * DTO для отображения информации о событии.
 * <p>
 * Содержит все основные поля события, необходимые для вывода клиенту.
 * Включает идентификаторы, описание, даты, статус и время создания.
 * </p>
 *
 * @see school.faang.user_service.entity.event.Event
 * @author JekaCAP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventViewDto {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private EventType type;
    private Long ownerId;
    private EventStatus status;
    private LocalDateTime createdAt;
}
