package school.faang.user_service.exception;

public class ExceptionMessageFormatter {
    public static String format(String template, Object... args) {
        for (Object arg : args) {
            template = template.replaceFirst("\\{}", arg == null ? "null" : arg.toString());
        }
        return template;
    }
}