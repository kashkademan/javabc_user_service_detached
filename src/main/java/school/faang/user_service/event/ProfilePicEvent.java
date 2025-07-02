package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePicEvent {
    private Long userId;
    private String newFileId;
    private String newSmallFileId;
    private String oldFileId;
    private String oldSmallFileId;
    private LocalDateTime changedAt;
}