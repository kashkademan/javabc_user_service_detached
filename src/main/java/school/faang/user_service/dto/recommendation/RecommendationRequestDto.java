package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

import static school.faang.user_service.dto.recommendation.RecommendationRequestConstraints.MAX_SIZE_STRING;
import static school.faang.user_service.dto.recommendation.RecommendationRequestConstraints.MESSAGE_SIZE_INVALID;

public record RecommendationRequestDto(
        Long id,
        @NotBlank
        @Size(max = MAX_SIZE_STRING, message = MESSAGE_SIZE_INVALID)
        String message,
        @NonNull
        UserDto requester,
        @NonNull
        UserDto receiver,
        @NonNull
        RequestStatus status,
        LocalDateTime createdAt
) {
}
