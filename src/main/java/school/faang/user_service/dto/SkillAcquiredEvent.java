package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillAcquiredEvent implements Serializable {
    private Long userId;
    private Long skillId;
}
