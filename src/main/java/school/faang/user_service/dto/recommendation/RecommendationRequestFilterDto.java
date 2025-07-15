package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.RequestStatus;

import static school.faang.user_service.dto.recommendation.RecommendationRequestConstraints.EITHER_REQUESTER_OR_RECEIVER_REQUIRED;
import static school.faang.user_service.dto.recommendation.RecommendationRequestConstraints.MAX_SIZE_STRING;
import static school.faang.user_service.dto.recommendation.RecommendationRequestConstraints.MESSAGE_SIZE_INVALID;

public record RecommendationRequestFilterDto(
        Long requesterId,
        Long receiverId,
        @Size(max = MAX_SIZE_STRING, message = MESSAGE_SIZE_INVALID)
        String messageContains,
        RequestStatus status
) {
    @AssertTrue(message = EITHER_REQUESTER_OR_RECEIVER_REQUIRED)
    public boolean isValid() {
        return requesterId != null || receiverId != null;
    }
}
