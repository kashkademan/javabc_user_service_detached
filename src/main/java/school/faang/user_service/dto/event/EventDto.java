package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import java.time.LocalDateTime;
import java.util.Set;

public record EventDto(
    Long id,
    String title,
    String description,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startDate,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime endDate,
    EventType type,
    Long ownerId,
    EventStatus status,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt,
    Set<String> skills,
    String name
) {}