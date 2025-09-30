package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
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

@RequiredArgsConstructor
@Service
@Slf4j
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Override
    @Transactional
    public GoalDto create(CreateGoalDto createGoalDto) {
        Long requesterId = userContext.getUserId();

        validateCreateGoalRequest(createGoalDto, requesterId);
        Goal goal = goalMapper.toGoal(createGoalDto);
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setUsers(new ArrayList<>());

        if (!ObjectUtils.isEmpty(createGoalDto.skillIds())) {
            if (createGoalDto.skillIds().size() != new HashSet<>(createGoalDto.skillIds()).size()) {
                throw new DataValidationException(
                        "SkillIds must contain only unique values: " + createGoalDto.skillIds()
                );
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

        if (!ObjectUtils.isEmpty(createGoalDto.parentGoalId())) {
            Goal parentGoal = goalRepository.findById(createGoalDto.parentGoalId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Parent Goal not found by id: %s".formatted(createGoalDto.parentGoalId())
                    ));
            goal.setParent(parentGoal);
        }

        if (ObjectUtils.isEmpty(createGoalDto.mentorId())) {
            User user = userRepository.findById(requesterId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found by id: %s".formatted(requesterId)));
            goal.getUsers().add(user);
        } else {
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

        goalRepository.save(goal);
        log.info("Goal entity saved: {}", goal);
        return goalMapper.toGoalDto(goal);
    }

    private void validateCreateGoalRequest(CreateGoalDto createGoalDto, Long requesterId) {
        if (!ObjectUtils.isEmpty(createGoalDto.deadline())
                && createGoalDto.deadline().isBefore(LocalDateTime.now().plusDays(1))) {
            throw new DataValidationException("Deadline date should provide at least 1 day for achievement");
        }

        if (!ObjectUtils.isEmpty(createGoalDto.mentorId())) {
            if (!requesterId.equals(createGoalDto.mentorId())) {
                throw new ForbiddenException(
                        "MentorId %s and requesterId %s must be the same"
                                .formatted(createGoalDto.mentorId(), requesterId)
                );
            }

            if (createGoalDto.userIds().contains(createGoalDto.mentorId())) {
                throw new ForbiddenException("Mentor cannot create goal for himself");
            }
        }

        if (ObjectUtils.isEmpty(createGoalDto.mentorId())) {
            if (createGoalDto.userIds().size() > 1) {
                throw new DataValidationException(
                        "When mentorId is empty, userIds size must be 1, actual: %s"
                                .formatted(createGoalDto.userIds().size())
                );
            }
            if (!ObjectUtils.nullSafeEquals(requesterId, createGoalDto.userIds().get(0))) {
                throw new ForbiddenException(
                        "RequesterId %s and userIds first id %s must be the same"
                                .formatted(requesterId, createGoalDto.userIds().get(0))
                );
            }
        }

        if (createGoalDto.userIds().size() != new HashSet<>(createGoalDto.userIds()).size()) {
            throw new DataValidationException("UserIds must contain only unique values: " + createGoalDto.userIds());
        }

        createGoalDto.userIds()
                .forEach(userId -> {
                    int activeGoalsCount = goalRepository.countActiveGoalsPerUser(userId);
                    if (activeGoalsCount >= 2) {
                        throw new DataValidationException("User has more than 2 active goals: %s"
                                .formatted(activeGoalsCount));
                    }
                });
    }
}
