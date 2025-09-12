package school.faang.user_service.rating_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.rating_service.entity.ActionType;
import school.faang.user_service.rating_service.entity.EventScore;

import java.util.Optional;

public interface EventScoreRepository extends JpaRepository<EventScore, Long> {
    Optional<EventScore> findByActionType(ActionType type);

    default EventScore findByActionTypeOrThrows(ActionType type) {
        return findByActionType(type)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("Действие type=%s не было найдено",
                                        type.name())
                        )
                );
    }
}