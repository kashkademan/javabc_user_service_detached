package school.faang.user_service.aspect.util;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Arrays;
import java.util.Optional;

public class AspectUtils {

    public static <T> T extractArgument(ProceedingJoinPoint joinPoint, Class<T> clazz) {
        return findArgumentOfType(joinPoint, clazz)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Argument of type %s not found in joinPoint: %s",
                                clazz.getSimpleName(), joinPoint.getSignature())
                ));
    }

    private static <T> Optional<T> findArgumentOfType(ProceedingJoinPoint joinPoint, Class<T> clazz) {
        return Arrays.stream(joinPoint.getArgs())
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst();
    }
}
