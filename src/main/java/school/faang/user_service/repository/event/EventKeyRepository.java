package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.event.EventKey;

public interface EventKeyRepository extends JpaRepository<EventKey, Long> {

    boolean existsByKey(String keyForKafka);
}
