package school.faang.user_service.rating_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.rating_service.entity.UserActionLog;

/**
 * Интерфейс для SQL-запросов для сущности {@link UserActionLog}
 *
 * @author Linempy
 * @since 29.08.2025
 */
public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO user_action_log (user_id, action_type, points_earned)
            VALUES (:userId, :actionType, :pointsEarned)
            """)
    void save(@Param("userId") Long userId,
              @Param("actionType") String  type,
              @Param("pointsEarned") Double pointsEarned);
}