package school.faang.user_service.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.redis.user.UserRedisModel;

@Repository
public interface UserRedisRepository extends CrudRepository<UserRedisModel, String> {
}
