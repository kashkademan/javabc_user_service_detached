package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    private Long id;
    @NotNull(message = "Author ID cannot be null")
    private Long authorId;
    @NotNull(message = "Receiver ID cannot be null")
    private Long receiverId;
    @NotBlank(message = "Recommendation text cannot be empty")
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}