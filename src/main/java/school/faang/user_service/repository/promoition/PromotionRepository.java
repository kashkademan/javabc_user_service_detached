package school.faang.user_service.repository.promoition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionStatus;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByPromotionStatusNot(PromotionStatus promotionStatus);

    boolean existsByUserId(Long userId);

    void deleteByUserId(Long userId);

    @Modifying
    @Query(value = """
            WITH locked_row AS (
                SELECT id FROM promotions WHERE id = :id FOR UPDATE
            )
            UPDATE promotions 
            SET remaining_display = remaining_display - 1 
            WHERE id = :id AND remaining_display >= 1
            """, nativeQuery = true)
    int decrementRemainingDisplay(@Param("id") Long id);

    @Modifying
    @Query(value = """
            UPDATE Promotion p 
            SET p.promotionStatus = 'ENDED'
            WHERE p.id = :id AND p.remainingDisplay <= 1
            """)
    int updateIfNoRemainingDisplay(@Param("id") Long id);

    Promotion getPromotionByUserId(Long userId);

}



