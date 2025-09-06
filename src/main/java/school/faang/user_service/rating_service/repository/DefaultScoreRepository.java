package school.faang.user_service.rating_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.rating_service.entity.ActionType;
import school.faang.user_service.rating_service.entity.DefaultScore;

public interface DefaultScoreRepository extends JpaRepository<DefaultScore, Long> {
    DefaultScore findByActionType(ActionType type);
}