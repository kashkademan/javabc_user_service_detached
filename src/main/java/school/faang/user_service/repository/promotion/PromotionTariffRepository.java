package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.PromotionTariff;

import java.util.List;

@Repository
public interface PromotionTariffRepository extends JpaRepository<PromotionTariff, Long> {

    List<PromotionTariff> findAllByDeletedFalse();
}
