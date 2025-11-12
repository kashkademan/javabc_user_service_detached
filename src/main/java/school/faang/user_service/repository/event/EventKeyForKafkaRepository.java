package school.faang.user_service.repository.event;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.event.EventKeyForKafka;

public interface EventKeyForKafkaRepository extends JpaRepository<EventKeyForKafka, Long> {

    boolean existsByKeyForKafka(String keyForKafka);
}
