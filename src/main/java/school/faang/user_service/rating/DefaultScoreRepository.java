package school.faang.user_service.rating;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DefaultScoreRepository — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Linempy
 * @since 29.08.2025
 */
public interface DefaultScoreRepository extends JpaRepository<DefaultScore, Long> {
}