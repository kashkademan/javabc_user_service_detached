package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.springframework.lang.Nullable;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record EventDto(
        @Nullable
        Long id,
        @Nullable
        String title,
        @Nullable
        String description,
        @Nullable @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startDate,
        @Nullable @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endDate,
        @Nullable
        EventType type,
        @Nullable
        Long ownerId,
        @Nullable
        EventStatus status,
        @Nullable @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @Nullable
        Set<String> skills,
        @Nullable
        String name
) {}