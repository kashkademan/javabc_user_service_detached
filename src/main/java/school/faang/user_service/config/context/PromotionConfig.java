package school.faang.user_service.config.context;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.promotion.Rate;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "promotion-redis")
@Component
public class PromotionConfig {
    private Map<String, Integer> promotions = new ConcurrentHashMap<>();
    private Duration ttl = Duration.ofDays(7);
    private String keyPrefix = "promotion:";
    private String userKeyPrefix = "promotions:user:";

    @PostConstruct
    public void init() {
        validatePromotions();
        log.info("Promotion config loaded: {}", promotions);
        log.info("Redis TTL: {}, Key prefix: {}", ttl, keyPrefix);
    }

    private void validatePromotions() {
        for (Rate rate : Rate.values()) {
            if (!promotions.containsKey(rate.name())) {
                log.warn("Rate {} not found in promotion configuration", rate);
            }
        }

        if (promotions.isEmpty()) {
            log.warn("No promotion rates configured!");
        }
    }

    public Integer getImpressionsForRate(Rate rate) {
        return promotions.get(rate.name());
    }

    public boolean isValidRate(Rate rate) {
        return promotions.containsKey(rate.name());
    }

    public String getPromotionKey(Long promotionId) {
        return keyPrefix + promotionId;
    }

    public String getUserPromotionsKey(Long userId) {
        return userKeyPrefix + userId;
    }
}

