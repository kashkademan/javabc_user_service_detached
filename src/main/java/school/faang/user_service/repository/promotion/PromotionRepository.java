package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionStatus;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    boolean existsByEventIdAndStatus(Long eventId, PromotionStatus status);

    boolean existsByUserIdAndStatus(Long userId, PromotionStatus status);
}
