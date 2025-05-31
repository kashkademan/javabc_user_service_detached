package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    @NotBlank(message = "Recommendation request has empty message.")
    private String message;
    private RequestStatus status;
    private List<Long> skillIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
