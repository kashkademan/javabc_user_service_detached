package school.faang.user_service.controller.recommendation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class RecommendationFilterDto {
    private String contentContains;
    private Long authorId;
    private Long receiverId;
}
