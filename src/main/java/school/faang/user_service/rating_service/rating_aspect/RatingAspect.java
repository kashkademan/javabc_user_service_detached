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
 * Аспект, автоматически отправляющий события рейтинга при выполнении методов,
 * аннотированных {@link RatingAction}.
 * <p>
 * После успешного выполнения метода:
 * <ul>
 *     <li>извлекает userId из {@link UserContext};</li>
 *     <li>формирует {@link UserActionEvent} на основе {@link RatingAction};</li>
 *     <li>отправляет событие через {@link RatingEventPublisher};</li>
 *     <li>логирует процесс и возможные ошибки.</li>
 * </ul>
 * Используется для отделения бизнес-логики от начисления рейтинга.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RatingAspect {

    private final UserContext userContext;
    private final RatingEventPublisher ratingEventPublisher;

    /**
     * Аспект, перехватывающий выполнение метода с аннотацией {@link RatingAction},
     * выполняющий исходный метод и отправляющий событие рейтинга.
     *
     * @param joinPoint    информация о перехваченном методе
     * @param ratingAction аннотация с типом действия
     * @return результат выполнения исходного метода
     * @throws Throwable пробрасывает исключения, возникшие при выполнении метода
     */
    @Around("@annotation(ratingAction)")
    public Object interceptAndSendEvent(ProceedingJoinPoint joinPoint, RatingAction ratingAction) throws Throwable {
        Object result = joinPoint.proceed();
        sendRatingEvent(ratingAction);
        return result;
    }

    /**
     * Формирует и отправляет событие рейтинга на основе аннотации {@link RatingAction}.
     * Оборачивает отправку в блок try-catch, чтобы не прерывать основное выполнение.
     *
     * @param ratingAction аннотация, содержащая тип действия
     */
    private void sendRatingEvent(RatingAction ratingAction) {
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
    }
}