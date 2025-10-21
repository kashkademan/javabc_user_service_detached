package school.faang.user_service.repository.promoition;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.promotion.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
}
