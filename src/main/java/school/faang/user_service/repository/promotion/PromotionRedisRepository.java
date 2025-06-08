package school.faang.user_service.repository.promotion;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;

import java.util.UUID;

@Repository
public interface PromotionRedisRepository extends CrudRepository<PromotionRedisModel, UUID> {
}
