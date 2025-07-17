package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateRecommendationDto {
    @NotNull
    private final Long authorId;
    @NotNull
    private final Long recieverId;
    @NotBlank
    private final String content;
}
