package school.faang.user_service.util;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

@UtilityClass
public class EntityUtil {

    public static <T> void setIfNotNull(T fieldValue, Consumer<T> setter) {
        if (Objects.nonNull(fieldValue)) {
            setter.accept(fieldValue);
        }
    }

    public static <T> void setIfTrue(T fieldValue, Predicate<T> predicate, Consumer<T> setter) {
        if (Objects.nonNull(fieldValue) && predicate.test(fieldValue)) {
            setter.accept(fieldValue);
        }
    }
}
