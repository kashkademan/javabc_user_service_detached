package school.faang.user_service.rating_service.rating_aspect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import school.faang.user_service.rating_service.config.KafkaRatingEventPublisher;

import java.time.Instant;

/**
 * DTO, представляющий событие пользовательского действия,
 * которое отправляется в Kafka для дальнейшей обработки рейтинговой системой.
 * <p>
 * Содержит идентификатор пользователя, тип действия и временную метку события.
 * <p>
 * Используется в {@link RatingAspect} и
 * {@link KafkaRatingEventPublisher} для передачи информации о действиях пользователей.
 */
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserActionEvent {
    private Long userId;
    private ActionType actionType;
    private Instant timestamp;
}