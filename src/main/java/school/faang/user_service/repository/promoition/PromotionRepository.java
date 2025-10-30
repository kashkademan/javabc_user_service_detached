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
            SET p.remainingImpressions = p.remainingImpressions - 1 
            WHERE p.id = :id        
            """)
    void decrementRemainingImpressions(@Param("id") Long id);
}


