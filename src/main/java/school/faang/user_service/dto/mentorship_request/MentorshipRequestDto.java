package school.faang.user_service.dto.mentorship_request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

public record MentorshipRequestDto(
        @NotEmpty(message = "Description cannot be empty")
        String description,

        @Min(value = 1, message = "id must be a positive number")
        Long requesterId, //ученик

        @Min(value = 1, message = "id must be a positive number")
        Long receiverId, //ментор

        RequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}