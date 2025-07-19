package school.faang.user_service.rating_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.rating_service.rating_aspect.ActionPointsRegistry;
import school.faang.user_service.rating_service.rating_aspect.ActionType;
import school.faang.user_service.rating_service.rating_aspect.UserActionEvent;

/**
 * Kafka Consumer, обрабатывающий события пользовательских действий {@link UserActionEvent}
 * из Kafka и обновляющий рейтинг пользователей в Redis.
 * <p>
 * Для каждого полученного события определяет количество баллов за действие с помощью {@link ActionPointsRegistry}
 * и увеличивает соответствующее значение в Redis Sorted Set по ключу {@code "leaderboard"}.
 * <p>
 * Если действие не поддерживается (баллы ≤ 0), событие игнорируется с предупреждением в логах.
 * <p>
 * Обработчик подписан на топик, указанный в настройках приложения {@code kafka.topics.user-actions}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RatingEventListener {

    /**
     * Регистр соответствия типа действия и количества баллов, начисляемых за него.
     */
    private final ActionPointsRegistry actionPointsRegistry;

    /**
     * Redis-шаблон для работы со строками, используется для обновления таблицы лидеров в Redis.
     */
    private final StringRedisTemplate redisTemplate;

    /**
     * Ключ Sorted Set в Redis, где хранится таблица лидеров (userId → сумма баллов).
     */
    private static final String LEADERBOARD_KEY = "leaderboard";

    /**
     * Метод, обрабатывающий событие {@link UserActionEvent} из Kafka.
     * <p>
     * Выполняет следующие шаги:
     * <ul>
     *     <li>Логирует полученное событие;</li>
     *     <li>Получает количество баллов за действие из {@link ActionPointsRegistry};</li>
     *     <li>Если баллы положительны, увеличивает значение score пользователя в Redis;</li>
     *     <li>Если баллы равны нулю или меньше, пишет предупреждение и игнорирует событие.</li>
     * </ul>
     *
     * @param event событие пользовательского действия с информацией о userId и типе действия
     */
    @KafkaListener(topics = "${kafka.topics.user-actions}", groupId = "rating-service")
    public void handle(UserActionEvent event) {
        log.info("Received event: {}", event);

        long userId = event.getUserId();
        ActionType actionType = event.getActionType();
        int points = actionPointsRegistry.getPoints(actionType);

        if (points <= 0) {
            log.warn("No points awarded: action type {} is not supported", actionType);
            return;
        }

        redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, String.valueOf(userId), points);
        log.info("Awarded {} points to user {} (action={})", points, userId, actionType);
    }
}
