package school.faang.user_service.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.redis.event.EventRedisModel;

import java.util.UUID;

@Repository
public interface EventRedisRepository extends CrudRepository<EventRedisModel, String> {
}
