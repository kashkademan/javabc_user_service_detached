package school.faang.user_service.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class PasswordGeneratorUtil {
    public String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}