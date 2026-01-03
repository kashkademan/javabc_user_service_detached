package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;
    private final UserValidator userValidator;
    private final UserService userService;
    private final SkillService skillService;

    @Override
    @Transactional
    public GoalDto create(CreateGoalDto createGoalDto) {
        userValidator.validatorUserExistence(userContext.getUserId());
        long userId = userContext.getUserId();

        if (createGoalDto.getMentorId() == null) {
            validActiveGoal(userId);
            checkingUserListEmpty(createGoalDto);
            User user = userService.findById(userId);
            List<Skill> userSkill = skillService.findAllById(createGoalDto.getSkillsToAchieveIds());
            Goal goalToSave = goalMapper.toGoal(createGoalDto);

            goalToSave.setUsers(List.of(user));
            goalToSave.setSkillsToAchieve(userSkill);
            goalToSave.setStatus(GoalStatus.ACTIVE);

            Goal result = goalRepository.save(goalToSave);
            return goalMapper.toGoalDto(result);
        }

        if (createGoalDto.getMentorId() == userId) {
            validActiveGoalList(createGoalDto.getUserIds());
            List<Skill> userSkill = skillService.findAllById(createGoalDto.getSkillsToAchieveIds());
            List<User> listMentee = userService.findAllById(createGoalDto.getUserIds());
            User mentor = userService.findById(userId);
            Goal goalToSave = goalMapper.toGoal(createGoalDto);

            goalToSave.setUsers(listMentee);
            goalToSave.setMentor(mentor);
            goalToSave.setSkillsToAchieve(userSkill);
            goalToSave.setStatus(GoalStatus.ACTIVE);

            Goal result = goalRepository.save(goalToSave);
            return goalMapper.toGoalDto(result);
        }

        throw new DataValidationException("Некорректные Данные!");
    }

    @Override
    @Transactional
    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        long userId = userContext.getUserId();
        Goal goal = findOrReturnGoal(goalId);
        if (goal.getStatus() == COMPLETED) {
            throw new DataValidationException("Цель нельзя обновить: цель завершена");
        }
        if (goal.getMentor() != null && userId != goal.getMentor().getId()) {
            checkHaveGoalUser(goal.getUsers(), userId);
            if (updateGoalDto.getStatus() == COMPLETED) {
                goal.setStatus(COMPLETED);
            }
        }

        goalMapper.update(goal, updateGoalDto);
        Goal goalUpdate = goalRepository.save(goal);
        return goalMapper.toGoalDto(goalUpdate);
    }

    @Override
    @Transactional
    public void delete(long goalId) {
        Goal goal = findOrReturnGoal(goalId);
        long userId = userContext.getUserId();
        userValidator.validatorUserExistence(userId);
        User user = userService.findById(userId);

        if (goal.getMentor() != null && user.getId().equals(goal.getMentor().getId())) {
            goalRepository.delete(goal);
            return;
        }
        checkHaveGoalUser(goal.getUsers(), userId);
        if (goal.getUsers().size() == 1) {
            goalRepository.delete(goal);
            return;
        }

        goal.getUsers().removeIf(u -> u.getId().equals(user.getId()));
        goalRepository.save(goal);
    }

    private Goal findOrReturnGoal(long goalId) {
        return goalRepository.findById(goalId).orElseThrow(() -> new DataValidationException("такой цели нет!"));
    }

    private void checkHaveGoalUser(List<User> users, long ownerRequestId) {
        users.stream()
                .filter(user -> user.getId() == ownerRequestId)
                .findFirst()
                .orElseThrow(() -> new DataValidationException("Вы не являетесь участником цели!"));
    }

    private void checkingUserListEmpty(CreateGoalDto createGoalDto) {
        if (!createGoalDto.getUserIds().isEmpty()) {
            throw new DataValidationException("Создавая цель для себя вы не можете присвоить к цели других пользователей!");
        }
    }

    private void validActiveGoal(long userId) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= 2) {
            throw new IllegalStateException("Пользователь не может иметь больше 2ух целей!");
        }
    }

    private void validActiveGoalList(List<Long> userIds) {
        userIds.forEach(id -> {
            int countActive = goalRepository.countActiveGoalsPerUser(id);
            if (countActive >= 2) {
                throw new IllegalStateException("У пользователя " + id + " больше 2 целей");
            }
        });
    }
}