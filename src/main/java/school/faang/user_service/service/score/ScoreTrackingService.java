package school.faang.user_service.service.score;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.model.score.ScoreEventSource;
import school.faang.user_service.model.score.UserScoreChangedEvent;

@RequiredArgsConstructor
@Component
@Slf4j
public class ScoreTrackingService {

    private final UserScoreService userScoreService;
    private final ApplicationEventPublisher eventPublisher;
    private final ScoreRuleService scoreRuleService;

    @Transactional
    public void trackAfterCompleteGoal(Goal goal) {
        int scoreDelta = scoreRuleService.getScore(ScoreActionType.COMPLETE_GOAL);

        for (User user : goal.getUsers()) {
            int newScore = userScoreService.incrementUserScore(user.getId(), scoreDelta);
            eventPublisher.publishEvent(new UserScoreChangedEvent(
                user.getId(),
                newScore,
                ScoreEventSource.GOAL_COMPLETED.name(),
                goal.getId()
            ));
        }
    }

    @Transactional
    public void trackAfterCompleteEvent(Event event) {
        if (event.getStatus() != EventStatus.COMPLETED) return;

        long eventId = event.getId();

        int attendeeScore = scoreRuleService.getParticipationScore(ScoreActionType.COMPLETE_EVENT);
        for (User attendee : event.getAttendees()) {
            int newScore = userScoreService.incrementUserScore(attendee.getId(), attendeeScore);
            eventPublisher.publishEvent(new UserScoreChangedEvent(
                attendee.getId(),
                newScore,
                ScoreEventSource.EVENT_COMPLETED_ATTENDEE.name(),
                eventId
            ));
        }

        int ownerScore = scoreRuleService.getOwnerScore(ScoreActionType.COMPLETE_EVENT);
        int newScore = userScoreService.incrementUserScore(event.getOwner().getId(), ownerScore);
        eventPublisher.publishEvent(new UserScoreChangedEvent(
            event.getOwner().getId(),
            newScore,
            ScoreEventSource.EVENT_COMPLETED_OWNER.name(),
            eventId
        ));
    }
}
