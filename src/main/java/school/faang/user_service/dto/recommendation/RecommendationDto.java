package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationDto(Long id,
                                Long authorId,
                                Long receiverId,

                                @NotBlank(message = "Field cannot be blank")
                                String content,

                                List<SkillOfferDto> skillOffers,
                                LocalDateTime createdAt)
{}
