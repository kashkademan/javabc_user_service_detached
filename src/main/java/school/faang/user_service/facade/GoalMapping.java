package school.faang.user_service.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalMapping {

    private final GoalService goalService;
    private final GoalMapper goalMapper;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public GoalDto mappingForCreate(CreateGoalDto createGoalDto) {
        Goal goal = goalMapper.toGoal(createGoalDto);
        List<User> userList = userRepository.findAllById(createGoalDto.userIds());
        List<Skill> skillList = skillRepository.findAllById(createGoalDto.skillIds());
        User mentor = userRepository.getByIdOrThrow(createGoalDto.mentorId());
        return goalMapper.toGoalDto(goalService.create(goal, userList, skillList, mentor));
    }

    public GoalDto mappingForUpdate(long goalId, UpdateGoalDto updateGoalDto) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);

        if (Objects.nonNull(updateGoalDto.skillIds())) {
            List<Skill> skillList = skillRepository.findAllById(updateGoalDto.skillIds());
            goal.setSkillsToAchieve(skillList);
        }

        GoalStatus goalStatus = updateGoalDto.status();
        long oldMentorId = goal.getMentor().getId();
        goalMapper.update(updateGoalDto, goal);

        if (updateGoalDto.mentorId() != null) {
            User mentor = userRepository.getByIdOrThrow(updateGoalDto.mentorId());
            goal.setMentor(mentor);
        }
        return goalMapper.toGoalDto(goalService.update(goal, goalStatus, oldMentorId, updateGoalDto.deadline()));
    }

    public List<GoalDto> mappingForFilters(GoalFilterDto filters) {
        return goalService.getByFilters(filters)
                .map(goalMapper::toGoalDto)
                .toList();
    }
}
