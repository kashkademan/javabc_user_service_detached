package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.FilterGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.IndexGoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.filter.goal.GoalFilterBuilderInterface;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.policy.goal.GoalCreatePolicy;
import school.faang.user_service.policy.goal.GoalDeletePolicy;
import school.faang.user_service.policy.goal.GoalUpdatePolicy;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final UserContext userContext;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final GoalFilterBuilderInterface<Goal, FilterGoalDto> goalFilter;
    private final GoalCreatePolicy goalCreatePolicy;
    private final GoalUpdatePolicy goalUpdatePolicy;
    private final GoalDeletePolicy goalDeletePolicy;

    @Override
    @Transactional
    public GoalDto create(CreateGoalDto createGoalDto) {
        goalCreatePolicy.validate(createGoalDto);
        Goal goal = goalMapper.toGoal(createGoalDto);

        List<User> users = userRepository.findAllById(createGoalDto.userIds());

        goal.setUsers(users);
        goal.setStatus(GoalStatus.ACTIVE);

        if (createGoalDto.skillIds() != null && !createGoalDto.skillIds().isEmpty()) {
            List<Skill> skills = skillRepository.findAllById(createGoalDto.skillIds());
            userRepository.findById(createGoalDto.mentorId())
                    .ifPresent(mentor -> {
                        List<UserSkillGuarantee> guarantees = new ArrayList<>();
                        skills.forEach(skill -> {
                            users.forEach(user -> {
                                UserSkillGuarantee userSkillGuarantee = new UserSkillGuarantee();
                                userSkillGuarantee.setUser(user);
                                userSkillGuarantee.setSkill(skill);
                                userSkillGuarantee.setGuarantor(mentor);
                                guarantees.add(userSkillGuarantee);
                            });
                        });
                        userSkillGuaranteeRepository.saveAll(guarantees);
                        goal.setMentor(mentor);
                    });
            goal.setSkillsToAchieve(skills);
        }

        goalRepository.save(goal);
        return goalMapper.toGoalDto(goal);
    }

    @Override
    public List<GoalDto> get(IndexGoalDto dto) {
        Specification<Goal> specification = goalFilter.buildSpecification(dto.filters(), null);
        List<Goal> goals = goalRepository.findAll(specification);
        return goalMapper.toGoalDtoList(goals);
    }

    @Override
    @Transactional
    public GoalDto update(Long id, UpdateGoalDto updateGoalDto) {
        Goal goal = goalRepository.getByIdOrThrow(id);
        goalUpdatePolicy.validate(updateGoalDto, goal);

        Optional.ofNullable(updateGoalDto.mentorId())
                .ifPresent(mentorId -> {
                    User mentor = userRepository.getByIdOrThrow(mentorId);
                    goal.setMentor(mentor);
                });

        Optional.ofNullable(updateGoalDto.skillIds())
                .ifPresent(skillIds -> {
                    List<Skill> skills = skillRepository.findAllById(skillIds);
                    goal.setSkillsToAchieve(skills);
                });

        goalMapper.update(goal, updateGoalDto);

        if (goal.getStatus() == GoalStatus.COMPLETED && goal.getSkillsToAchieve() != null) {
            for (Skill skill : goal.getSkillsToAchieve()) {
                for (User user : goal.getUsers()) {
                    skillRepository.assignSkillToUser(skill.getId(), user.getId());
                }
            }
        }

        Goal updatedGoal = goalRepository.save(goal);
        return goalMapper.toGoalDto(updatedGoal);
    }

    @Override
    public void delete(Long id) {
        Goal goal = goalRepository.getByIdOrThrow(id);
        goalDeletePolicy.validate(goal);
        long usersSize = goal.getUsers().size();
        long currentUserId = userContext.getUserId();
        boolean isMentor = goal.getMentor() != null
                && goal.getMentor().getId().equals(currentUserId);
        if (isMentor || usersSize <= 1) {
            goalRepository.deleteById(id);
        } else {
            goalRepository.deleteUserFromGoal(currentUserId, goal.getId());
        }
    }
}
