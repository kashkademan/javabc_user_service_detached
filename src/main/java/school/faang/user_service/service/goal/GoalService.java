package school.faang.user_service.service.goal;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
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

    public GoalDto createGoal(GoalDto goalDto) {
        User user = getAndValidateAUser();
        List<Skill> skills = getAndValidateSkills(goalDto.getSkillsToAchieve());
        
        Goal goalToSave = Goal.builder()
            .title(goalDto.getTitle())
            .description(goalDto.getDescription())
            .status(GoalStatus.ACTIVE)
            .users(List.of(user))
            .skillsToAchieve(skills)
            .build();
        Goal savedGoal = goalRepository.save(goalToSave);

        return goalMapper.toDto(savedGoal);
    }

    public GoalDto updateGoal(GoalDto goalDto) {
        Goal goal = getGoalData(goalDto.getId());
        List<Skill> skills = getAndValidateSkills(goalDto.getSkillsToAchieve());

        // Денис, вот этот код не работает который закомментил :(
        // for (Skill skill : goal.getSkillsToAchieve()) {
        //     goalRepository.deleteSkillFromGoal(goal.getId(), skill.getId());
        // }
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

    public void deleteGoal() {

    }

    public void findSubtasksByGoalId() {

    }

    public void findGoalsByUserId() {

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

    private void updateUsersSkills(List<Skill> skills, List<User> users) {
        for (Skill skill : skills) {
            for (User user : users) {
                // Денис, вот этот код не работает который закомментил :(
                // почему то тут юзер не обновляется :(
                List<Skill> userSkills = user.getSkills();
                userSkills.add(skill);
                user.setSkills(userSkills);
                userRepository.save(user);
            }
        }
    }
}
