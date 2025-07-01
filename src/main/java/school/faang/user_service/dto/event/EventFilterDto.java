package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventType;

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