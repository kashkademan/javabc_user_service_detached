package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import school.faang.user_service.aspect.UserScore;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalCreateByMentorDto;
import school.faang.user_service.dto.goal.GoalCreateByUserDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;
import school.faang.user_service.validator.goal.GoalValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static school.faang.user_service.entity.user.ActionType.GOAL_COMPLETED;
import static school.faang.user_service.entity.user.ActionType.GOAL_CREATED_BY_MENTOR;
import static school.faang.user_service.entity.user.ActionType.GOAL_CREATED_BY_USER;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalService {

    @Value("${goal.max-active-goals}")
    private int maxGoalsPerUser;

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private final GoalMapper goalMapper;
    private final UserContext userContext;

    @UserScore(type = GOAL_CREATED_BY_USER)
    @Transactional
    public GoalDto createByUser(GoalCreateByUserDto goalCreateByUserDto) {
        GoalValidator.validateCreateGoalByUser(goalCreateByUserDto);
        Goal goal = goalMapper.toGoal(goalCreateByUserDto);
        createGoalByUser(goal, goalCreateByUserDto);
        goalRepository.save(goal);

        return goalMapper.toGoalDto(goal);
    }

    @UserScore(type = GOAL_CREATED_BY_MENTOR)
    @Transactional
    public GoalDto createByMentor(GoalCreateByMentorDto goalCreateByMentorDto) {
        GoalValidator.validateCreateGoalByMentor(goalCreateByMentorDto);
        Goal goal = goalMapper.toGoal(goalCreateByMentorDto);
        createGoalByMentor(goal, goalCreateByMentorDto);
        goalRepository.save(goal);

        return goalMapper.toGoalDto(goal);
    }

    @Transactional
    public void delete(long goalId) {
        Goal goalToDelete = goalRepository.getByIdOrThrow(goalId);

        if (ObjectUtils.isEmpty(goalToDelete.getMentor())) {
            deleteGoalByUser(goalToDelete);
        } else {
            deleteGoalByMentor(goalToDelete);
        }
    }

    public List<GoalDto> getByFilters(GoalFilterDto filters) {
        Long requesterId = userContext.getUserId();
        userRepository.getByIdOrThrow(requesterId);

        Predicate<Goal> predicate = goal -> true;

        if (filters.titleContains() != null) {
            predicate = predicate.and(goal ->
                    StringUtils.containsIgnoreCase(goal.getTitle(), filters.titleContains()));
        }
        if (filters.descriptionContains() != null) {
            predicate = predicate.and(goal ->
                    StringUtils.containsIgnoreCase(goal.getDescription(), filters.descriptionContains()));
        }
        if (filters.status() != null) {
            predicate = predicate.and(goal -> filters.status().equals(goal.getStatus()));
        }
        if (filters.mentorId() != null) {
            predicate = predicate.and(goal ->
                    goal.getMentor() != null && filters.mentorId().equals(goal.getMentor().getId()));
        }

        return goalRepository.findAll().stream()
                .filter(predicate)
                .map(goalMapper::toGoalDto)
                .toList();
    }

    @UserScore(type = GOAL_COMPLETED)
    public GoalDto update(long goalId, GoalUpdateDto goalUpdateDto) {
        Long requesterId = userContext.getUserId();
        Goal goalToUpdate = goalRepository.getByIdOrThrow(goalId);
        GoalValidator.validateUpdateGoal(goalUpdateDto, requesterId, goalToUpdate);
        goalMapper.update(goalUpdateDto, goalToUpdate);
        goalRepository.save(goalToUpdate);

        return goalMapper.toGoalDto(goalToUpdate);
    }

    private void deleteGoalByUser(Goal goalToDelete) {
        Long requesterId = userContext.getUserId();
        Long goalToDeleteId = goalToDelete.getId();

        if (goalRepository.isParent(goalToDeleteId)) {
            throw new ForbiddenException("Goal %s is parent and cannot be delete".formatted(goalToDeleteId));
        }

        List<User> users = goalToDelete.getUsers();
        boolean userBelongsToGoal = users.stream().anyMatch(u -> Objects.equals(u.getId(), requesterId));

        if (!userBelongsToGoal) {
            throw new ForbiddenException("User %s cannot delete goal %s".formatted(requesterId, goalToDeleteId));
        }

        if (users.size() == 1) {
            goalRepository.deleteById(goalToDeleteId);
        } else {
            goalRepository.deleteUserFromGoal(requesterId, goalToDeleteId);
        }
    }

    private void deleteGoalByMentor(Goal goalToDelete) {
        Long requesterId = userContext.getUserId();

        User mentor = goalToDelete.getMentor();
        List<Skill> skills = goalToDelete.getSkillsToAchieve();

        if (!Objects.equals(mentor.getId(), requesterId)) {
            throw new ForbiddenException("MentorId %s and requesterId %s are different"
                    .formatted(mentor.getId(), requesterId));
        }

        goalRepository.deleteById(goalToDelete.getId());

        if (!skills.isEmpty()) {
            List<Long> skillIds = skills.stream()
                    .map(Skill::getId)
                    .toList();
            userSkillGuaranteeRepository.deleteBySkillIdIn(skillIds);
        }
    }

    private void createGoalByMentor(Goal goal, GoalCreateByMentorDto goalCreateByMentorDto) {
        Long requesterId = userContext.getUserId();
        User mentor = userRepository.getByIdOrThrow(requesterId);
        goal.setMentor(mentor);
        List<Long> userIds = goalCreateByMentorDto.userIds();
        validateActiveGoalsCountByUsers(userIds);

        List<User> users = userRepository.findAllById(userIds);

        if (users.size() != userIds.size()) {
            Set<Long> foundIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());

            List<Long> missing = userIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new EntityNotFoundException("Users not found by userIds: " + missing);
        }

        addSkillsToGoalIfExists(goalCreateByMentorDto.skillIds(), goal);
        addParentGoalIfExists(goalCreateByMentorDto.parentGoalId(), goal);
        goal.getUsers().addAll(users);

        if (!ObjectUtils.isEmpty(goal.getSkillsToAchieve())) {
            List<UserSkillGuarantee> guarantees = goal.getSkillsToAchieve()
                    .stream()
                    .flatMap(s -> users.stream()
                            .map(u -> {
                                UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
                                userSkillGuarantee.setUser(u);
                                userSkillGuarantee.setGuarantor(mentor);
                                userSkillGuarantee.setSkill(s);
                                return userSkillGuarantee;
                            })
                    )
                    .toList();

            userSkillGuaranteeRepository.saveAll(guarantees);
        }
        goal.setStatus(GoalStatus.ACTIVE);
    }

    private void createGoalByUser(Goal goal, GoalCreateByUserDto goalCreateByUserDto) {
        Long requesterId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(requesterId);
        validateActiveGoalsCountByUser(requesterId);
        goal.getUsers().add(user);

        addSkillsToGoalIfExists(goalCreateByUserDto.skillIds(), goal);
        addParentGoalIfExists(goalCreateByUserDto.parentGoalId(), goal);
        goal.setStatus(GoalStatus.ACTIVE);
    }

    private void addSkillsToGoalIfExists(List<Long> skillIds, Goal goal) {
        if (!ObjectUtils.isEmpty(skillIds)) {
            if (skillIds.size() != new HashSet<>(skillIds).size()) {
                throw new DataValidationException("Skill IDs must contain only unique values: %s".formatted(skillIds));
            }

            List<Skill> skills = skillRepository.findAllById(skillIds);

            if (skills.size() != skillIds.size()) {
                Set<Long> foundIds = skills.stream()
                        .map(Skill::getId)
                        .collect(Collectors.toSet());

                List<Long> missing = skillIds.stream()
                        .filter(id -> !foundIds.contains(id))
                        .toList();

                throw new EntityNotFoundException("Skills not found by ids: " + missing);
            }

            goal.getSkillsToAchieve().addAll(skills);
        }
    }

    private void addParentGoalIfExists(Long parentGoalId, Goal goal) {
        if (!ObjectUtils.isEmpty(parentGoalId)) {
            Goal parentGoal = goalRepository.getByIdOrThrow(parentGoalId);
            goal.setParent(parentGoal);
        }
    }

    private void validateActiveGoalsCountByUser(Long userId) {
        int activeGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoalsCount >= maxGoalsPerUser) {
            throw new DataValidationException("User has %s active goals".formatted(maxGoalsPerUser));
        }
    }

    private void validateActiveGoalsCountByUsers(List<Long> userIds) {
        userIds.forEach(this::validateActiveGoalsCountByUser);
    }
}
