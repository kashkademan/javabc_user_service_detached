package school.faang.user_service.validation;

import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class ValidationUtils {

    public static void executeIfNotNull(Object field, Runnable runnable) {
        if (Objects.nonNull(field)) {
            runnable.run();
        }
    }
}
