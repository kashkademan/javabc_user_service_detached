package school.faang.user_service.repository.promotion;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.redis.promotion.EventPromotionRedis;

@Repository
public interface EventPromotionRedisRepository extends CrudRepository<EventPromotionRedis, String> {
}
