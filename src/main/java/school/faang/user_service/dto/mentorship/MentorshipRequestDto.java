package school.faang.user_service.dto.mentorship;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@FieldNameConstants
@Builder
public record MentorshipRequestDto(
        Long id,
        String description,
        UserDto requester,
        UserDto receiver,
        RequestStatus status,
        LocalDateTime createdAt
) {
}