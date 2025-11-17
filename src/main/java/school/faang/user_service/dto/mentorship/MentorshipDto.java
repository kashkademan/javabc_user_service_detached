package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class MentorshipDto {
    private final Long mentorId;
    private final Long menteeId;
    private LocalDateTime createdAt;
}
