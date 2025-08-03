package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RecommendationViewDto(Long id, Long authorId, @NotNull(message = "Id не может быть null") Long receiverId,
                                    @NotBlank(message = "Сообщение не может быть пустым") String content,
                                    LocalDate dateOfRecommendation) {
}
