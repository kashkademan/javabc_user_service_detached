package school.faang.user_service.service.score;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.entity.Role;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.model.user.RoleThesaurus;
import school.faang.user_service.service.role.RoleService;

@RequiredArgsConstructor
@Component
@Slf4j
public class ScoreTrackingService {

    private final UserScoreService userScoreService;
    private final ScoreRuleService scoreRuleService;
    private final LeaderboardService leaderboardService;
    private final RoleService roleService;

    @Transactional
    public void trackAfterCompleteGoal(Goal goal) {
        int scoreDelta = scoreRuleService.getScoreByType(ScoreActionType.COMPLETE_GOAL);

        for (User user : goal.getUsers()) {
            int newScore = userScoreService.updateScore(user.getId(), scoreDelta);
            leaderboardService.updateLeaderboard(user.getId(), newScore);
        }
    }

    @Transactional
    public void trackAfterCompleteEvent(Event event) {
        if (event.getStatus() != EventStatus.COMPLETED) {
            return;
        }

        Role attendeeRole = roleService.getByNameOrThrow(RoleThesaurus.ATTENDEE);
        int attendeeScore = scoreRuleService.getScoreByRole(ScoreActionType.COMPLETE_EVENT, attendeeRole.getName().name());
        for (User attendee : event.getAttendees()) {
            int newScore = userScoreService.updateScore(attendee.getId(), attendeeScore);
            leaderboardService.updateLeaderboard(attendee.getId(), newScore);
        }

        Role ownerRole = roleService.getByNameOrThrow(RoleThesaurus.OWNER);
        int ownerScore = scoreRuleService.getScoreByRole(ScoreActionType.COMPLETE_EVENT, ownerRole.getName().name());
        int newScore = userScoreService.updateScore(event.getOwner().getId(), ownerScore);
        leaderboardService.updateLeaderboard(event.getOwner().getId(), newScore);
    }
}
