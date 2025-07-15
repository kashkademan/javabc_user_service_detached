package school.faang.user_service.repository.premium;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;

public interface PremiumRepository extends JpaRepository<Premium, Long> {

    boolean existsByUserId(long userId);

    List<Premium> findAllByEndDateBefore(LocalDateTime endDate);

    List<Premium> findAllByEndDateAfter(LocalDateTime date);

    @Query("SELECT p FROM Premium p WHERE p.endDate > CURRENT_TIMESTAMP")
    List<Premium> findActivePremiums();
}
