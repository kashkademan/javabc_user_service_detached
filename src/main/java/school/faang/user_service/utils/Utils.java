package school.faang.user_service.utils;

import org.apache.logging.log4j.message.FormattedMessage;

public class Utils {
    public static String format(final String messagePattern, final Object... arguments) {
        return new FormattedMessage(messagePattern, arguments).getFormattedMessage();
    }

    private Utils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
