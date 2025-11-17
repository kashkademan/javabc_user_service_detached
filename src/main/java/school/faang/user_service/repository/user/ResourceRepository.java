package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.resource.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

}