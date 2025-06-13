package school.faang.user_service.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3KeyGenerator {
    private static final String IMAGE_KEY_PATTERN = "%s/user_%s/%s-%s-%s";

    public String generateKey(String fileName, S3Folder folder, long userId) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String uniqueId = UUID.randomUUID().toString();

        return String.format(IMAGE_KEY_PATTERN, folder, userId, timeStamp, uniqueId, fileName);
    }
}
