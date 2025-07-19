package school.faang.user_service.rating_service.rating_aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.rating_service.config.RatingEventPublisher;

import java.time.Instant;

/**
 * Аспект, перехватывающий методы, аннотированные {@link RatingAction}, для автоматической отправки событий рейтинга.
 * <p>
 * При вызове метода с аннотацией {@code @RatingAction} после успешного выполнения:
 * <ul>
 *     <li>получает текущий userId из контекста {@link UserContext};</li>
 *     <li>формирует событие {@link UserActionEvent} с типом действия из аннотации и текущим timestamp;</li>
 *     <li>отправляет событие через {@link RatingEventPublisher} (обычно в Kafka);</li>
 *     <li>логирует процесс и ошибки при отправке.</li>
 * </ul>
 * <p>
 * Позволяет отделить логику начисления рейтинга от бизнес-логики, не засоряя методы вручную вызываемым кодом.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RatingAspect {

    /**
     * Контекст пользователя, из которого извлекается идентификатор текущего пользователя.
     */
    private final UserContext userContext;

    /**
     * Публикатор событий рейтинга (например, отправка в Kafka).
     */
    private final RatingEventPublisher ratingEventPublisher;

    /**
     * Перехватывает выполнение методов с аннотацией {@link RatingAction}, выполняет метод,
     * затем отправляет событие рейтинга с информацией о пользователе и типе действия.
     *
     * @param pjp           точка входа в метод (JoinPoint)
     * @param ratingAction  аннотация, содержащая тип действия рейтинга
     * @return результат выполнения метода
     * @throws Throwable пробрасывает исключения метода дальше
     */
    @Around("@annotation(ratingAction)")
    public Object interceptAndSendEvent(ProceedingJoinPoint pjp, RatingAction ratingAction) throws Throwable {
        Object result = pjp.proceed();

        try {
            Long userId = userContext.getUserId();
            ActionType actionType = ratingAction.value();
            UserActionEvent event = UserActionEvent.builder()
                    .userId(userId)
                    .actionType(actionType)
                    .timestamp(Instant.now())
                    .build();

            log.info("Sending an event to Kafka: {}", event);
            ratingEventPublisher.send(event);

        } catch (Exception ex) {
            log.error("Error when sending a rating event", ex);
        }

        return result;
    }
}
