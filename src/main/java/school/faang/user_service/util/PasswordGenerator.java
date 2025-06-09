package school.faang.user_service.util;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@UtilityClass
@Component
public class PasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_LENGTH = 12;
    private final SecureRandom random = new SecureRandom();

    public String generatePassword() {
        StringBuilder password = new StringBuilder(DEFAULT_LENGTH);
        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }
}
