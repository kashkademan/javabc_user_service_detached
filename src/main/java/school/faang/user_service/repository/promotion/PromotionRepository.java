package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("""
    SELECT COUNT(*) > 0
    FROM Promotion p
    WHERE p.event_id = :event_id AND p.status = 'ACTIVE'
    """)
    boolean existsActivePromotionByEvent(@Param("event_id") Long eventId);

    @Query("""
            SELECT COUNT(*) > 0
            FROM Promotion p
            WHERE p.user_id = :user_id AND p.status = 'ACTIVE'
            """)
    boolean existsActivePromotionByUser(@Param("user_id") Long userId);
}
