package school.faang.user_service.repository.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.redis.Leaderboard;

@Repository
public interface UserRedisRepository extends CrudRepository<Leaderboard, String> {
}
