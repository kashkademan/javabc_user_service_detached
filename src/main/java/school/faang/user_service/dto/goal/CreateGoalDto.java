package school.faang.user_service.dto.goal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateGoalDto {
    private String title;
    private String description;
    private LocalDateTime deadline;
    private Long mentorId;
    private List<Long> userIds = new ArrayList<>();
}
