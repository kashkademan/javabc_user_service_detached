package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProfileViewedEventDto {
    private String viewerName;
    private Long viewerId;
    private Long viewedId;
    private final LocalDateTime localDateTime = LocalDateTime.now();
}
