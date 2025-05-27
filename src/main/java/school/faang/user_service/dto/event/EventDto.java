package school.faang.user_service.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Event entity")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    @Schema(description = "Unique id of event", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "Title of the event", example = "Title", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String title;
    @Schema(description = "Start date of the event", example = "2025-01-01T00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Schema(description = "Owner id of the event", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long ownerId;
    private String description;
    private List<Long> relatedSkills;
    private String location;
    private int maxAttendees;
    private EventType eventType;
    private EventStatus eventStatus;
}
