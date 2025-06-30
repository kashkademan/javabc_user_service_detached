package school.faang.user_service.service.score;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.kafka.producer.UserScoreProducer;
import school.faang.user_service.model.score.UserScoreChangedEvent;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreTrackingService {

    private final UserService userService;
    private final UserScoreProducer kafkaProducer;

    @Transactional
    public void trackAfterCompleteGoal(Goal goal, int scoreDelta) {
        log.info("Начато начисление очков за выполнение цели. GoalId={}, Delta={}", goal.getId(), scoreDelta);

        for (User user : goal.getUsers()) {
            long userId = user.getId();
            userService.incrementUserScore(userId, scoreDelta);
            kafkaProducer.sendScoreChanged(new UserScoreChangedEvent(
                userId,
                scoreDelta,
                "GOAL_COMPLETED",
                goal.getId()
            ));
        }

        log.info("Завершено начисление очков за цель goalId={}", goal.getId());
    }

    @Transactional
    public void trackAfterCompleteEvent(Event event, int scoreDelta) {
        if (event.getStatus() != EventStatus.COMPLETED) {
            log.debug("Пропущено начисление очков: событие eventId={} не завершено. Статус={}", event.getId(), event.getStatus());
            return;
        }

        long eventId = event.getId();

        for (User attendee : event.getAttendees()) {
            long userId = attendee.getId();
            userService.incrementUserScore(userId, scoreDelta);
            kafkaProducer.sendScoreChanged(new UserScoreChangedEvent(
                    userId,
                    scoreDelta,
                    "EVENT_COMPLETED_ATTENDEE",
                    eventId
            ));
            log.info("Очки начислены участнику userId={}, eventId={}", userId, eventId);
        }

        long ownerId = event.getOwner().getId();
        userService.incrementUserScore(ownerId, scoreDelta);
        kafkaProducer.sendScoreChanged(new UserScoreChangedEvent(
                ownerId,
                scoreDelta,
                "EVENT_COMPLETED_OWNER",
                eventId
        ));
        log.info("Очки начислены владельцу userId={}, eventId={}", ownerId, eventId);
    }
}
