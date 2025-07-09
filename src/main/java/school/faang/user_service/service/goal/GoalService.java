package school.faang.user_service.service.goal;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;
    private final List<GoalFilter> goalFilters;

    public GoalDto createGoal(GoalDto goalDto) {
        User user = getAndValidateAUser();
        List<Skill> skills = getAndValidateSkills(goalDto.getSkillsToAchieve());
        Goal parentGoal = (goalDto.getParent() == null) ? null : getGoalData(goalDto.getParent());
        
        Goal goalToSave = Goal.builder()
            .title(goalDto.getTitle())
            .description(goalDto.getDescription())
            .status(GoalStatus.ACTIVE)
            .users(List.of(user))
            .skillsToAchieve(skills)
            .parent(parentGoal)
            .build();
        Goal savedGoal = goalRepository.save(goalToSave);

        return goalMapper.toDto(savedGoal);
    }

    public GoalDto updateGoal(GoalDto goalDto) {
        Goal goal = getGoalData(goalDto.getId());
        List<Skill> skills = getAndValidateSkills(goalDto.getSkillsToAchieve());

        goal.setSkillsToAchieve(Collections.emptyList());
        goalRepository.save(goal);

        if (checkStatus(goal, goalDto)) {
            goal.setStatus(GoalStatus.COMPLETED);
            updateUsersSkills(skills, goal.getUsers());
        }

        goal.setTitle(null != goalDto.getTitle() ? goalDto.getTitle() : goal.getTitle());
        goal.setDescription(null != goalDto.getDescription() ? goalDto.getDescription() : goal.getDescription());
        goal.setSkillsToAchieve(skills);
        Goal updatedGoal = goalRepository.save(goal);

        return goalMapper.toDto(updatedGoal);
    }

    public void deleteGoal(Long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto goalFilterDto) {
        Stream<Goal> subtasks = goalRepository.findByParent(goalId);

        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(goalFilterDto)) {
                subtasks = goalFilter.apply(subtasks, goalFilterDto);
            }
        }

        return subtasks.map(goalMapper::toDto).toList();
    }

    @Transactional
    public List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto goalFilterDto) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);

        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(goalFilterDto)) {
                goals = goalFilter.apply(goals, goalFilterDto);
            }
        }

        return goals.map(goalMapper::toDto).toList();
    }

    public GoalDto getGoalById(Long goalId) {
        return goalMapper.toDto(getGoalData(goalId));
    }

    private User getAndValidateAUser() {
        User user = userRepository.findById(userContext.getUserId()).orElseThrow(() -> {
            log.warn("User is not registered {}.", userContext.getUserId());
            return new EntityNotFoundException("User is not registered.");
        });
        if (user.getGoals().size() >= 3) {
            log.error("User has reached the maximum number of goals.");
            throw new IllegalArgumentException("User has reached the maximum number of goals.");
        }
        
        return user;
    }

    private List<Skill> getAndValidateSkills(List<Long> skillIds) {
        List<Skill> skills = skillRepository.findAllById(skillIds);
        if (skills.isEmpty()) {
            log.error("No skills registered. Cannot create goal without skills.");
            throw new IllegalArgumentException("No skills registered. Cannot create goal without skills.");
        }

        return skills;
    }

    private boolean checkStatus(Goal goal, GoalDto goalDto) {
        return goalDto.getStatus().equals(GoalStatus.COMPLETED) && goal.getStatus().equals(GoalStatus.ACTIVE);
    }

    private Goal getGoalData(Long goalId) {
        return goalRepository.findById(goalId).orElseThrow(() -> {
            log.warn("Goal is not registered {}.", goalId);
            return new EntityNotFoundException("Goal is not registered.");
        });
    }

    @Transactional
    public void updateUsersSkills(List<Skill> skills, List<User> users) {
        if (skills == null || users == null) return;
    
        for (Skill skill : skillRepository.findAllById(
            skills.stream().map(Skill::getId).collect(Collectors.toList())
        )) {
            Set<Long> existingUserIds = skill.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    
            List<User> usersToAdd = users.stream()
                .filter(user -> !existingUserIds.contains(user.getId()))
                .collect(Collectors.toList());
    
            if (!usersToAdd.isEmpty()) {
                skill.getUsers().addAll(usersToAdd);
            }
        }
    }
}
