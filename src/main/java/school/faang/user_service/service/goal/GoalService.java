package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    public GoalDto create(CreateGoalDto createGoalDto) {
        Long requesterId = userContext.getUserId();

        validateCreateGoalRequest(createGoalDto, requesterId);

        Goal goal = goalMapper.toGoal(createGoalDto);
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setUsers(new ArrayList<>());

        addSkillsToGoalIfExists(createGoalDto, goal);
        addParentGoalIfExists(createGoalDto, goal);

        if (ObjectUtils.isEmpty(createGoalDto.mentorId())) {
            createGoalByUser(requesterId, goal);
        } else {
            createGoalByMentor(requesterId, goal, createGoalDto);
        }

        goalRepository.save(goal);
        return goalMapper.toGoalDto(goal);
    }

    @Transactional
    public void delete(long goalId) {
        Long requesterId = userContext.getUserId();
        Goal goalToDelete = goalRepository.getByIdOrThrow(goalId);

        if (goalRepository.findByParent(goalId).findAny().isPresent()) {
            throw new ForbiddenException("Goal %s is parent and cannot be delete".formatted(goalId));
        }

        if (ObjectUtils.isEmpty(goalToDelete.getMentor())) {
            deleteGoalByUser(requesterId, goalToDelete);
        } else {
            deleteGoalByMentor(requesterId, goalToDelete);
        }
    }

    public List<GoalDto> getByFilters(GoalFilterDto filters) {
        Long requesterId = userContext.getUserId();
        userRepository.findById(requesterId).orElseThrow(() -> new EntityNotFoundException("User %s not found".formatted(requesterId)));

        return goalRepository.findAll().stream()
                .filter(goal ->
                        filters.titleContains() == null || StringUtils.containsIgnoreCase(goal.getTitle(), filters.titleContains())
                )
                .filter(goal ->
                        filters.descriptionContains() == null || StringUtils.containsIgnoreCase(goal.getDescription(), filters.descriptionContains())
                )
                .filter(goal ->
                        filters.status() == null || filters.status().equals(goal.getStatus())
                )
                .filter(goal ->
                        filters.mentorId() == null || (goal.getMentor() != null && filters.mentorId().equals(goal.getMentor().getId()))
                )
                .map(goalMapper::toGoalDto)
                .toList();
    }

    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        Long requesterId = userContext.getUserId();
        Goal goalToUpdate = goalRepository.getByIdOrThrow(goalId);

        validateUpdateGoalRequest(updateGoalDto, requesterId, goalToUpdate);

        goalMapper.update(updateGoalDto, goalToUpdate);
        goalRepository.save(goalToUpdate);
        return goalMapper.toGoalDto(goalToUpdate);
    }

    private void deleteGoalByUser(long requesterId, Goal goalToDelete) {
        List<User> users = goalToDelete.getUsers();

        boolean belongsToGoal = users.stream()
                .anyMatch(u -> Objects.equals(u.getId(), requesterId));

        if (!belongsToGoal) {
            throw new ForbiddenException("User %s cannot delete goal %s".formatted(requesterId, goalToDelete.getId()));
        }

        if (users.size() > 1) {
            goalRepository.deleteUserFromGoal(requesterId, goalToDelete.getId());
        } else {
            goalRepository.deleteById(goalToDelete.getId());
        }
    }

    private void deleteGoalByMentor(long requesterId, Goal goalToDelete) {
        User mentor = goalToDelete.getMentor();
        List<Skill> skills = goalToDelete.getSkillsToAchieve();

        if (!Objects.equals(mentor.getId(), requesterId)) {
            throw new ForbiddenException("MentorId %s and requesterId %s are different".formatted(mentor.getId(), requesterId));
        }

        goalRepository.deleteById(goalToDelete.getId());

        if (!skills.isEmpty()) {
            List<Long> skillIds = skills.stream()
                    .map(Skill::getId)
                    .toList();
            userSkillGuaranteeRepository.deleteBySkillIdIn(skillIds);
        }
    }

    private void createGoalByMentor(long requesterId, Goal goal, CreateGoalDto createGoalDto) {
        User mentor = userRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: %s".formatted(requesterId)));

        List<User> users = createGoalDto.userIds()
                .stream()
                .map(userId -> userRepository.findById(userId).orElseThrow(
                        () -> new EntityNotFoundException("User not found by id: %s".formatted(userId)))
                )
                .toList();
        goal.setMentor(mentor);
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
    }

    private void createGoalByUser(long requesterId, Goal goal) {
        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by id: %s".formatted(requesterId)));
        goal.getUsers().add(user);
    }

    private void addSkillsToGoalIfExists(CreateGoalDto createGoalDto, Goal goal) {
        if (!ObjectUtils.isEmpty(createGoalDto.skillIds())) {
            if (createGoalDto.skillIds().size() != new HashSet<>(createGoalDto.skillIds()).size()) {
                throw new DataValidationException("SkillIds must contain only unique values: %s".formatted(createGoalDto.skillIds()));
            }

            List<Skill> skills = createGoalDto.skillIds()
                    .stream()
                    .map(skillId -> skillRepository.findById(skillId).orElseThrow(
                            () -> new EntityNotFoundException("Skill not found by id: %s".formatted(skillId)))
                    )
                    .toList();

            goal.setSkillsToAchieve(new ArrayList<>());
            goal.getSkillsToAchieve().addAll(skills);
        }
    }

    private void addParentGoalIfExists(CreateGoalDto createGoalDto, Goal goal) {
        if (!ObjectUtils.isEmpty(createGoalDto.parentGoalId())) {
            Goal parentGoal = goalRepository.findById(createGoalDto.parentGoalId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Parent Goal not found by id: %s".formatted(createGoalDto.parentGoalId())
                    ));
            goal.setParent(parentGoal);
        }
    }

    private void validateUpdateGoalRequest(UpdateGoalDto updateGoalDto, Long requesterId, Goal goalToUpdate) {
        if (ObjectUtils.nullSafeEquals(goalToUpdate.getStatus(), GoalStatus.COMPLETED)) {
            throw new DataValidationException("Cannot update - Goal is already completed");
        }

        if (!ObjectUtils.isEmpty(updateGoalDto.deadline())
                && updateGoalDto.deadline().isBefore(LocalDateTime.now().plusDays(1))) {
            throw new DataValidationException("Deadline date should provide at least 1 day for achievement");
        }

        User mentor = goalToUpdate.getMentor();

        if (ObjectUtils.isEmpty(mentor)) {
            List<User> users = goalToUpdate.getUsers();
            if (users.size() > 1) {
                throw new DataValidationException("User cannot update goal if there are other participants");
            }
            if (!ObjectUtils.nullSafeEquals(requesterId, users.get(0).getId())) {
                throw new ForbiddenException(
                        "RequesterId %s and userIds first id %s must be the same".formatted(requesterId, users.get(0).getId())
                );
            }
        } else {
            if (ObjectUtils.isEmpty(updateGoalDto.mentorId())) {
                throw new ForbiddenException("MentorId required");
            }
            if (!requesterId.equals(updateGoalDto.mentorId()) || !requesterId.equals(mentor.getId())) {
                throw new ForbiddenException("Mentor %s does not belong to goal".formatted(requesterId));
            }
        }
    }


    private void validateCreateGoalRequest(CreateGoalDto createGoalDto, Long requesterId) {
        if (!ObjectUtils.isEmpty(createGoalDto.deadline())
                && createGoalDto.deadline().isBefore(LocalDateTime.now().plusDays(1))) {
            throw new DataValidationException("Deadline date should provide at least 1 day for achievement");
        }

        if (!ObjectUtils.isEmpty(createGoalDto.mentorId())) {
            if (!requesterId.equals(createGoalDto.mentorId())) {
                throw new ForbiddenException(
                        "MentorId %s and requesterId %s must be the same".formatted(createGoalDto.mentorId(), requesterId)
                );
            }

            if (createGoalDto.userIds().contains(createGoalDto.mentorId())) {
                throw new ForbiddenException("Mentor cannot create goal for himself");
            }
        }

        if (ObjectUtils.isEmpty(createGoalDto.mentorId())) {
            if (createGoalDto.userIds().size() > 1) {
                throw new DataValidationException(
                        "When mentorId is empty, userIds size must be 1, actual: %s".formatted(createGoalDto.userIds().size())
                );
            }
            if (!ObjectUtils.nullSafeEquals(requesterId, createGoalDto.userIds().get(0))) {
                throw new ForbiddenException(
                        "RequesterId %s and userIds first id %s must be the same".formatted(requesterId, createGoalDto.userIds().get(0))
                );
            }
        }

        if (createGoalDto.userIds().size() != new HashSet<>(createGoalDto.userIds()).size()) {
            throw new DataValidationException(
                    "UserIds must contain only unique values: %s".formatted(createGoalDto.userIds())
            );
        }

        createGoalDto.userIds()
                .forEach(userId -> {
                    int activeGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
                    if (activeGoalsCount >= 2) {
                        throw new DataValidationException("User has more than 2 active goals: %s".formatted(activeGoalsCount));
                    }
                });
    }
}
