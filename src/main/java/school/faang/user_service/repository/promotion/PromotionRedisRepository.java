package school.faang.user_service.repository.promotion;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;

import java.util.Optional;

@Repository
public interface PromotionRedisRepository extends CrudRepository<PromotionRedisModel, String>
        ,
        PromotionRedisRepositoryNative
{
    Optional<PromotionRedisModel> findByEventId(Long eventId);

    Optional<PromotionRedisModel> findByUserId(Long userId);
}
