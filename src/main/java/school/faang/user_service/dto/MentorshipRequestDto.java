package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class MentorshipRequestDto {
    private Long id;

    @Size(max = 4096, message = "Description length must be less than 4096 symbols")
    @NotBlank(message = "Mentorship description could not be blank")
    private String description;

    @NotNull(message = "Mentorship requester id could not be null")
    private Long requesterId;

    @NotNull(message = "Mentorship receiver id could not be null")
    private Long receiverId;

    @Null(message = "Request status will be set automatically")
    private RequestStatus requestStatus;

    @Null(message = "Rejection reason will be set automatically")
    private String rejectionReason;

    @Null(message = "Creation date time will be set automatically")
    private LocalDateTime createdAt;

    @Null(message = "Update date time will be set automatically")
    private LocalDateTime updatedAt;
}