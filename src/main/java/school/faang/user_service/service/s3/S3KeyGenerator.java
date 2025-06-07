package school.faang.user_service.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.exception.authorization.UserUnauthorizedException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3KeyGenerator {
    private static final String IMAGE_KEY_PATTERN = "%s/user_%s/%s-%s-%s";
    private static final String SYSTEM = "system";

    private final UserContext userContext;

    public String generateKey(String fileName, S3Folder folder) {
        String user = getUserIdOrSystem();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String uniqueId = UUID.randomUUID().toString();
        String sanitizedFileName = sanitize(fileName);

        return String.format(IMAGE_KEY_PATTERN, folder, user, timeStamp, uniqueId, sanitizedFileName);
    }

    // TODO: как получить userId, если вызывать ассинхронно?
    private String getUserIdOrSystem() {
        try {
            return String.valueOf(userContext.getUserId());
        } catch (UserUnauthorizedException ex) {
            return SYSTEM;
        }
    }

    private String sanitize(String fileName) {
        return fileName.replaceAll("[^\\p{L}\\p{N}._\\-\\— ]", "_");
    }
}
