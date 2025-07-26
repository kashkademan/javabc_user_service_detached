package school.faang.user_service.rating_service.rating_aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для маркировки методов, за выполнение которых
 * следует начислять рейтинговые очки.
 * <p>
 * Используется в AOP-аспекте для перехвата вызовов методов и
 * генерации соответствующих событий {@link ActionType}.
 * <p>
 * Применяется к методам (ElementType.METHOD) и сохраняется в runtime,
 * чтобы аспект мог её обнаружить.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RatingAction {
    ActionType value();
}