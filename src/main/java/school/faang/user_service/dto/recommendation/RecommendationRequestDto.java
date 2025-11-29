package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import lombok.Getter;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Builder
@Getter
public class RecommendationRequestDto {
    private Long id;
    private String message;
    private UserDto requester;
    private UserDto receiver;
    private RequestStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
