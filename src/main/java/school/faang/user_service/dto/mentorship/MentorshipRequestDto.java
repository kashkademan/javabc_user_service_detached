package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class MentorshipRequestDto {
    @NotNull
    private Long requesterId;
    @NotNull
    private Long receiverId;
    @NotBlank(message = "Description is required")
    private String description;
    private RequestStatus status;
}