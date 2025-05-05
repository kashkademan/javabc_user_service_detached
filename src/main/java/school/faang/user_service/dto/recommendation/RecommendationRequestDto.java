package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class RecommendationRequestDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private String message;
    private String status;
    private List<Long> skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void addSkill(Long skillId) {
        if (skills == null) {
            skills = new ArrayList<>();
        }
        skills.add(skillId);
    }
}
