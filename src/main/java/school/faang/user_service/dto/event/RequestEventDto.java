package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestEventDto {
    private Long id;
    @NotBlank(message = "Event title must not be empty")
    private String title;
    @NotNull(message = "StartDate must not be null")
    private LocalDateTime startDate;
    @NotNull(message = "EndDate must not be null")
    private LocalDateTime endDate;
    @NotNull(message = "Owner Id must not be null")
    private Long ownerId;
    @NotBlank(message = "Event description must not be empty")
    private String description;
    private List<Long> relatedSkills;
    @NotBlank(message = "Event location must not be empty")
    private String location;
    private int maxAttendees;
    @NotNull(message = "Event type must not be null")
    private EventType eventType;
    @NotNull(message = "Event status must not be null")
    private EventStatus eventStatus;
}
