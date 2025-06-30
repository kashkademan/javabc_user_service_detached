package school.faang.user_service.model.score;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserScoreChangedEvent {
    private long userId;
    private int updatedScore;
    private String sourceType;
    private long sourceId;
}
