package school.faang.user_service.rating;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RatingRepository — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Linempy
 * @since 29.08.2025
 */
public interface RatingRepository extends JpaRepository<UserActions, Long> {

}