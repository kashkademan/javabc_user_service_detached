package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

public record MentorshipRequestViewDto(
        @NotNull Long id,
        @NotNull @Size(min = 2, max = 100) String description,
        @NotNull UserDto requester,
        @NotNull UserDto receiver,
        @NotNull RequestStatus status
) {
}