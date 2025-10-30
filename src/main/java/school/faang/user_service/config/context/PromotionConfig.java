package school.faang.user_service.config.context;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.entity.promotion.Tarif;
import school.faang.user_service.exception.ForbiddenException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "promotion-redis")
@Configuration
public class PromotionConfig {

    private static String KEY_PREFIX = "promotion:";
    private static String USER_KEY_PREFIX = "promotions:user:";

    private Map<String, Integer> promotions = new ConcurrentHashMap<>();
    @Value("${promotion-redis.ttl}")
    private Duration ttl;


    @PostConstruct
    public void init() {
        validatePromotions();
        log.info("Promotion config loaded: {}", promotions);
        log.info("Redis TTL: {}, Key prefix: {}", ttl, KEY_PREFIX);
    }

    private void validatePromotions() {
        for (Tarif tarif : Tarif.values()) {
            if (!promotions.containsKey(tarif.name())) {
                throw new ForbiddenException(String.format("Rate %s not found in promotion configuration", tarif));
            }
        }

        if (promotions.isEmpty()) {
            throw new ForbiddenException("No promotion tarif configured!");
        }
    }

    public Integer getImpressionsForTarif(Tarif tarif) {
        return promotions.get(tarif.name());
    }
}

