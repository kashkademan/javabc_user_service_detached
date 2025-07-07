package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class MentorshipRequestDto {

    private final Long id;
    private final String description;
    private final UserDto requester;
    private final UserDto receiver;
    private final RequestStatus status;
    private final String phone;
    private final String aboutMe;
    private final LocalDateTime createdAt;
    private final String rejectionReason;
}
