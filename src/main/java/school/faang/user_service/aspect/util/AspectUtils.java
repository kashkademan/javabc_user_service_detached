package school.faang.user_service.aspect.util;

import org.aspectj.lang.ProceedingJoinPoint;

public class AspectUtils {

    private AspectUtils() {

    }

    public static <T> T requireArgumentOfType(ProceedingJoinPoint joinPoint, Class<T> clazz) {
        T value = findArgumentOfType(joinPoint, clazz);
        if (value == null) {
            throw new IllegalArgumentException("Argument of type " + clazz.getSimpleName() + " not found in join point");
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private static <T> T findArgumentOfType(ProceedingJoinPoint joinPoint, Class<T> clazz) {
        for (Object arg : joinPoint.getArgs()) {
            if (clazz.isInstance(arg)) {
                return (T) arg;
            }
        }
        return null;
    }
}
