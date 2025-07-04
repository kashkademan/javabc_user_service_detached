package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventType;

/**
 * DTO для фильтрации событий при поиске.
 * <p>
 *     Используется для передачи параметров фильтрации, таких как часть названия,
 *     описание, идентификаторы владельца и участника, а также тип события.
 *     Все поля не обязательные.
 * </p>
 *
 * @see school.faang.user_service.entity.event.Event
 * @author JekaCAP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDto {

    @Size(max = 100, message = "Title filter must be at most 100 characters")
    private String titleContains;

    @Size(max = 255, message = "Description filter must be at most 255 characters")
    private String descriptionContains;

    private Long ownerId;
    private Long participantId;
    private EventType type;
}