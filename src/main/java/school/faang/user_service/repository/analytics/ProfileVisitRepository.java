package school.faang.user_service.repository.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.analytics.ProfileVisit;

import java.util.List;

/**
 * ProfileVisitRepository — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
public interface ProfileVisitRepository extends JpaRepository<ProfileVisit, Long> {
    @Query("""
            SELECT pv FROM ProfileVisit pv
            WHERE pv.visitedId = :visitedId
            ORDER BY visitedAt DESC
            LIMIT :limit
            OFFSET :offset
            """
    )
    public List<ProfileVisit> findAllByVisitedIdAndLimitAbdOffset(long visitedId, int limit, int offset);
}
