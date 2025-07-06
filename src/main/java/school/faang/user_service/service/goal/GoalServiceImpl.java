package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.UserServiceException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.messaging.publishers.GoalAttachedMessagePublisher;
import school.faang.user_service.messaging.publishers.GoalCompletedMessagePublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    @Value("${logic.constants.max_active_goals}")
    private int maximumAllowedActiveGoals;
    private final GoalMapper goalMapper;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final GoalInvitationRepository goalInvitationRepository;
    private final List<GoalFilter> goalFilters;
    private final GoalCompletedMessagePublisher goalCompletedMessagePublisher;
    private final GoalAttachedMessagePublisher goalAttachedMessagePublisher;

    @Override
    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        long usersActiveGoals = goalRepository.findGoalsByUserId(userId)
                .filter(g -> GoalStatus.ACTIVE == g.getStatus())
                .count();

        if (usersActiveGoals >= maximumAllowedActiveGoals) {
            throw new UserServiceException(
                    String.format("User id: %d has Maximum allowed active goals - %d",
                            userId,
                            maximumAllowedActiveGoals));
        }

        int existing = skillRepository.countExisting(goalDto.getSkillIds());
        if (existing < goalDto.getSkillIds().size()) {
            throw new IllegalArgumentException("Not existing skill ids provided");
        }

        Goal createdGoal = goalRepository.create(
                goalDto.getTitle(),
                goalDto.getDescription(),
                goalDto.getParentId()
        );

        addGoalToUser(userId, createdGoal);
        addGoalToSkills(createdGoal);

        goalAttachedMessagePublisher.createAndPublishMessage(createdGoal, userId);

        return goalMapper.toGoalDTO(createdGoal);
    }

    @Override
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(EntityNotFoundException::new);

        boolean goalWasCompleted = goalToUpdate.getStatus() == GoalStatus.COMPLETED;
        boolean goalSetCompleted = goalDto.getStatus() == GoalStatus.COMPLETED;
        if (goalSetCompleted && goalWasCompleted)
            throw new IllegalStateException("Goal was already completed");

        List<Long> existingSkillIds = skillRepository.findAllById(goalDto.getSkillIds()).stream()
                .map(Skill::getId)
                .toList();
        List<Long> missingSkillIds = goalDto.getSkillIds().stream()
                .filter(skillId -> !existingSkillIds.contains(skillId))
                .toList();
        if (!missingSkillIds.isEmpty())
            throw new IllegalArgumentException("Skill ids not exists: %s".formatted(missingSkillIds));

        goalMapper.updateGoalFromDto(goalDto, goalToUpdate);
        Long parentId = goalDto.getParentId();
        if (parentId != null) {
            Optional<Goal> updatedParentOpt = goalRepository.findById(parentId);
            updatedParentOpt.ifPresent(goalToUpdate::setParent);
        }
        List<Long> updatedSkillIds = goalDto.getSkillIds();
        if (null != updatedSkillIds && !updatedSkillIds.isEmpty()) {
            goalToUpdate.setSkillsToAchieve(skillRepository.findAllById(updatedSkillIds));
        }
        goalToUpdate.setUpdatedAt(LocalDateTime.now());
        goalRepository.save(goalToUpdate);

        if (GoalStatus.COMPLETED == goalDto.getStatus()) {
            updateUsersWithSkills(goalToUpdate);
            goalCompletedMessagePublisher.publishMessage(goalToUpdate);
        }

        return goalMapper.toGoalDTO(goalToUpdate);
    }


    private void updateUsersWithSkills(Goal completedGoal) {
        List<User> users = completedGoal.getUsers();
        List<Skill> skills = completedGoal.getSkillsToAchieve();
        users.forEach(user -> {
            HashSet<Skill> merged = new HashSet<>(skills);
            merged.addAll(user.getSkills());
            user.setSkills(new ArrayList<>(merged));
        });
        skills.forEach(skill -> {
            HashSet<User> merged = new HashSet<>(users);
            merged.addAll(skill.getUsers());
            skill.setUsers(new ArrayList<>(merged));
        });
        skillRepository.saveAllAndFlush(skills);
        userRepository.saveAllAndFlush(users);
    }

    @Override
    @Transactional
    public GoalDto deleteGoal(long goalId) {
        Goal goalToDelete = goalRepository.findById(goalId)
                .orElseThrow(EntityNotFoundException::new);
        deleteGoalCascade(goalToDelete);
        goalRepository.delete(goalToDelete);

        return goalMapper.toGoalDTO(goalToDelete);
    }

    private void deleteGoalCascade(Goal goalToDelete) {
        List<User> users = goalToDelete.getUsers();
        users.forEach(user -> user.getGoals().remove(goalToDelete));
        userRepository.saveAllAndFlush(users);

        List<Skill> skills = goalToDelete.getSkillsToAchieve();
        skills.forEach(skill -> skill.getGoals().remove(goalToDelete));
        skillRepository.saveAllAndFlush(skills);

        List<GoalInvitation> invitations = goalToDelete.getInvitations();
        invitations.forEach(invitation -> invitation.setGoal(null));
        goalInvitationRepository.saveAllAndFlush(invitations);
    }

    @Override
    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        Stream<Goal> goalsByParent = goalRepository.findByParent(goalId);
        List<Goal> goals = filterGoals(goalsByParent, filter);
        return goalMapper.toGoalDTOs(goals);
    }

    @Override
    @Transactional
    public List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filter) {
        Stream<Goal> goalsByUserId = goalRepository.findGoalsByUserId(userId);
        List<Goal> goals = filterGoals(goalsByUserId, filter);
        return goalMapper.toGoalDTOs(goals);
    }

    private List<Goal> filterGoals(Stream<Goal> goalStream, GoalFilterDto filterDto) {
        List<GoalFilter> applicableFilters = goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filterDto))
                .toList();

        return goalStream
                .filter(goal -> applicableFilters.stream().allMatch(goalFilter -> goalFilter.doFilter(goal, filterDto)))
                .toList();
    }

    private User addGoalToUser(Long userId, Goal createdGoal) {
        User userById = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User id: " + userId));
        return addGoalToUser(userById, createdGoal);
    }

    private User addGoalToUser(User user, Goal createdGoal) {
        List<Goal> goals = user.getGoals();
        goals.add(createdGoal);
        user.setGoals(goals);
        return userRepository.saveAndFlush(user);
    }

    private void addGoalToSkills(Goal createdGoal) {
        List<Skill> skillsToUpdateWithNewGoal = createdGoal.getSkillsToAchieve();
        skillsToUpdateWithNewGoal.forEach(skill -> {
            List<Goal> skillGoals = skill.getGoals();
            skillGoals.add(createdGoal);
            skill.setGoals(skillGoals);
        });
        skillRepository.saveAllAndFlush(skillsToUpdateWithNewGoal);
    }

}
