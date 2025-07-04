package school.faang.user_service.service.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;

@Component
public interface GoalService {

    GoalDto create(GoalCreateDto goalCreateDto);

    GoalDto update(long goalId, GoalUpdateDto goalUpdateDto);

    GoalDto getById(long goalId);

    void  delete(long goalId);
}
