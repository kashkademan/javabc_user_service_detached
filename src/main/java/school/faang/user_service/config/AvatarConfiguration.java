package school.faang.user_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "picture-provider")
@Getter
@Setter
public class AvatarConfiguration {
    private String randomPictureProviderRootUrl;
    private String defaultSmallAvatarSeed;

    private int imageLimitSize;
    private int bigImageLimit;
    private int smallImageLimit;
    private String bucketSubstorage;
}
