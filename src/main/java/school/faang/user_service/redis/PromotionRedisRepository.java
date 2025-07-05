package school.faang.user_service.redis;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromotionRedisRepository extends CrudRepository<PromotionRedisModel, String>
//        ,
//        PromotionRedisRepositoryNative
{
    Optional<PromotionRedisModel> findByEventId(Long eventId);

    Optional<PromotionRedisModel> findByUserId(Long userId);
}
