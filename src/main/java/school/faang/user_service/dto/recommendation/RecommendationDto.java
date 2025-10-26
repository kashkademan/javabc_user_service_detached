package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecommendationDto {
    private Long id;
    private Long authorId;
    private Long receiverId;
    private String content;
}
