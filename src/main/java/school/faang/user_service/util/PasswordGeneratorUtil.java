package school.faang.user_service.util;

import org.apache.commons.lang3.RandomStringUtils;

public class PasswordGeneratorUtil {
    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}