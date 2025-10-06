package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.goal.FilterGoal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_GOALS_FOR_ONE_USER = 2;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final List<FilterGoal> filterGoals;
    private final GoalMapper goalMapper;

    public Goal create(Goal goal, List<Long> userIds,
                       List<Long> skillIds, Long mentorId) {

        List<User> userList = userRepository.findAllById(userIds);

        userList.forEach(user -> {
            if (user.getGoals() != null) {
                long totalGoal = user.getGoals().stream()
                        .filter(g -> Objects.equals(g.getStatus(), ACTIVE))
                        .count();
                if (totalGoal >= MAX_GOALS_FOR_ONE_USER) {
                    throw new DataValidationException(String.format("The user {} already has {} goals",
                            user.getId(), MAX_GOALS_FOR_ONE_USER));
                }
            }
        });

        List<Skill> skillList = skillRepository.findAllById(skillIds);

        checkDealLineGoals(goal.getDeadline());
        User mentor = userRepository.getByIdOrThrow(mentorId);
        goal.setSkillsToAchieve(skillList);
        goal.setStatus(ACTIVE);
        goal.setMentor(mentor);
        goal.setUsers(userList);
        goalRepository.save(goal);
        log.info("The goal has been added to the database. id - {}, tittle - {}", goal.getId(), goal.getTitle());
        return goal;
    }

    @Transactional
    public Goal update(Long goalId, UpdateGoalDto updateGoalDto) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);

        if (Objects.nonNull(updateGoalDto.skillIds())) {
            List<Skill> skillList = skillRepository.findAllById(updateGoalDto.skillIds());
            goal.setSkillsToAchieve(skillList);
        }


        if (updateGoalDto.mentorId() != null) {
            User mentor = userRepository.getByIdOrThrow(updateGoalDto.mentorId());
            goal.setMentor(mentor);
        }

        long oldMentorId = goal.getMentor().getId();
        GoalMapper.update(updateGoalDto, goal);

        isParticipantInTheGoal(oldMentorId, goal);
        LocalDateTime dateTime = updateGoalDto.deadline();
        if (Objects.nonNull(dateTime)) {
            checkDealLineGoals(dateTime);
            goal.setDeadline(dateTime);
        }

        GoalStatus goalStatus = updateGoalDto.status();
        checkGoalStatus(goal, goalStatus);

        goalRepository.save(goal);
        log.info("the goal id - {}, tittle - {} was changed", goal.getId(), goal.getTitle());
        return goal;
    }


    public void delete(long goalId) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        Long mentorId = goal.getMentor().getId();
        Long userId = userContext.getUserId();

        isParticipantInTheGoal(mentorId, goal);

        if (Objects.equals(userId, mentorId) || goal.getUsers().size() == 1) {
            goalRepository.deleteById(goalId);
            log.info("The user with {} has been removed from the goal id-{}'s participants", userId, goalId);
        } else {
            goalRepository.deleteUserFromGoal(userId, goalId);
            log.info("the goal id - {}, tittle - {} was deleted", goalId, goal.getTitle());
        }
    }

    public Stream<Goal> getByFilters(GoalFilterDto filters) {
        Stream<Goal> goalsStream = goalRepository.findAll().stream();

        for (FilterGoal filterGoal : filterGoals) {
            if (filterGoal.isApplication(filters)) {
                goalsStream = filterGoal.apply(goalsStream, filters);
            }
        }
        log.info("A list of goals for the specified filter has been created.");
        return goalsStream;
    }

    private void isParticipantInTheGoal(long mentorId, Goal goal) {
        Long userId = userContext.getUserId();

        List<Long> usersIdByGoal = goal.getUsers().stream()
                .map(User::getId)
                .toList();

        if (!Objects.equals(userId, mentorId) && !usersIdByGoal.contains(userId)) {
            throw new ForbiddenException(String.format("The user with ID - {} is not a mentor or participant "
                            + "of the Goal. Goal title - {}, goal id - {}",
                    userId, goal.getTitle(), goal.getId()));
        }
    }

    private void checkDealLineGoals(LocalDateTime goalDateTime) {
        LocalDateTime dataNow = LocalDateTime.now();
        if (dataNow.isAfter(goalDateTime)) {
            throw new DataValidationException(String.format("the deadline {} cannot be earlier than "
                    + "the current date {}", goalDateTime, dataNow));
        }
    }

    private void checkGoalStatus(Goal goal, GoalStatus goalStatus) {
        Long userId = userContext.getUserId();

        if (Objects.equals(goal.getStatus(), COMPLETED)) {
            throw new ForbiddenException(String.format("Goal title - {}, goal id - {} has already been completed",
                    goal.getTitle(), goal.getId()));
        }

        if (Objects.equals(goalStatus, COMPLETED) && !Objects.equals(userId, goal.getMentor().getId())) {
            throw new ForbiddenException(String.format("Only a mentor can complete the goal (title - {}, goal id - {})",
                    goal.getTitle(), goal.getId()));
        }

        if (Objects.equals(goalStatus, COMPLETED)) {
            Set<Long> skillsToAssign = new HashSet<>(goal.getSkillsToAchieve()).stream()
                    .map(Skill::getId)
                    .collect(Collectors.toSet());
            goal.getUsers().forEach(user -> {
                skillsToAssign
                        .forEach(skillId -> skillRepository.assignSkillToUser(skillId, user.getId()));
            });
        }
    }
}
