package school.faang.user_service.dto.mentorship_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.RequestStatus;

public record MentorshipRequestFilterDto(

        @Size(max = 255)
        @NotBlank(message = "Field cannot be blank")
        String description,

        @NotNull(message = "Field cannot be null")
        Long requesterId,

        @NotNull(message = "Field cannot be null")
        Long receiverId,

        @NotNull(message = "Field cannot be null")
        RequestStatus status
) {
}
