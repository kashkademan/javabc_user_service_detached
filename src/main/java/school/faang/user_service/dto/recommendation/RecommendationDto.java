package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecommendationDto {
    private final Long id;
    private final Long authorId;
    @NotNull(message = "Id не может быть null")
    private final Long receiverId;
    @NotBlank(message = "Сообщение не может быть пустым")
    private final String content;
    private final Data dateOfRecommendation;
}
