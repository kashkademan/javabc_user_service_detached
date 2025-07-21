package school.faang.user_service.rating_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.rating_service.rating_aspect.ActionType;
import school.faang.user_service.rating_service.rating_aspect.UserActionEvent;

/**
 * Kafka Consumer, обрабатывающий события пользовательских действий {@link UserActionEvent}
 * из Kafka и обновляющий рейтинг пользователей в Redis.
 * <p>
 * Для каждого полученного события извлекается тип действия {@link ActionType},
 * на основе которого определяется количество баллов (через {@link ActionType#getPoints()}).
 * Если баллы положительные, значение рейтинга пользователя обновляется в Redis Sorted Set
 * по ключу {@code "leaderboard"}.
 * <p>
 * Если действие не поддерживается (баллы ≤ 0), событие игнорируется с предупреждением в логах.
 * <p>
 * Обработчик подписан на Kafka-топик, заданный в настройках приложения через свойство
 * {@code kafka.topics.user-actions}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RatingEventListener {

    private final StringRedisTemplate redisTemplate;
    private static final String LEADERBOARD_KEY = "leaderboard";

    @KafkaListener(topics = "${kafka.topics.user-actions}", groupId = "rating-service")
    public void handle(UserActionEvent event) {
        log.info("Received event: {}", event);

        long userId = event.getUserId();
        ActionType actionType = event.getActionType();
        int points = actionType.getPoints();

        if (points <= 0) {
            log.warn("No points awarded: action type {} is not supported", actionType);
            return;
        }

        redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, String.valueOf(userId), points);
        log.info("Awarded {} points to user {} (action={})", points, userId, actionType);
    }
}