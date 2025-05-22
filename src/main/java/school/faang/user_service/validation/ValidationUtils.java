package school.faang.user_service.validation;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

@UtilityClass
public class ValidationUtils {

    public static void executeIfNotNull(Object field, Runnable runnable) {
        if (Objects.nonNull(field)) {
            runnable.run();
        }
    }

    public static <T> void setIfNotNull(T fieldValue, Consumer<T> setter) {
        if (Objects.nonNull(fieldValue)) {
            setter.accept(fieldValue);
        }
    }

    public static <T> void setIfNotNullAndTrue(T fieldValue, Predicate<T> predicate, Consumer<T> setter) {
        if (Objects.nonNull(fieldValue) && predicate.test(fieldValue)) {
            setter.accept(fieldValue);
        }
    }
}
