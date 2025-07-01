package school.faang.user_service.repository.promotion;

public interface PromotionRedisRepositoryNative {
    long decrementCountView(String key);
}
