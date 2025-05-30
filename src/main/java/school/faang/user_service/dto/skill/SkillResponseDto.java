package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponseDto {
    private Long id;
    private String title;
    private List<Long> usersIds;
    private List<Long> userSkillGuaranteesIds;
    private List<Long> eventsIds;
    private List<Long> goalsIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
