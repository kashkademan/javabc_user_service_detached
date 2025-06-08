package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RecommendationRequestDto(

    @NotBlank(message = "Message cannot be blank")
    String message,

    @NotNull(message = "Status cannot be null")
    String status,

    @NotNull(message = "Field cannot be null")
    @NotEmpty(message = "Skills cannot be empty")
    List<String> skills,

    @NotNull(message = "Requester ID cannot be null")
    @Min(value = 1, message = "id must be a positive number")
    Long requesterId,

    @NotNull(message = "Receiver ID cannot be null")
    @Min(value = 1, message = "id must be a positive number")
    Long receiverId,

    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}