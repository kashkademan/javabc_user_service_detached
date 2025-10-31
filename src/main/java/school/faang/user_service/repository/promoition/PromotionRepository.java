package school.faang.user_service.repository.promoition;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.promotion.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    boolean existsByUserId(Long userId);

    void deleteByUserId(Long userId);

    @Modifying
    @Query(value = """
            UPDATE Promotion p
            SET p.remainingDisplay = p.remainingDisplay - 1 
            WHERE p.id = :id AND p.remainingDisplay >= 1      
            """)
    int decrementRemainingImpressions(@Param("id") Long id);

    @Modifying
    @Query(value = """
         DELETE FROM Promotion p 
         WHERE p.id = :id AND p.remainingDisplay <= 1
         """)
    int deleteIfNoRemainingImpressions(@Param("id") Long id);
}


