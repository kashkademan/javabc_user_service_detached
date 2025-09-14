package school.faang.user_service.rating_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.rating_service.dto.user.UserScoreProjection;
import school.faang.user_service.rating_service.entity.UserScore;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для SQL-запросов связанных с сущностью {@link UserScore}
 *
 * @author Linempy
 * @since 05.09.2025
 */
public interface UserScoreRepository extends JpaRepository<UserScore, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE user_score
            SET score = score + :increment
            WHERE user_id = :userId
            """)
    void setIncrement(@Param("userId") Long userId,
                      @Param("increment") Double increment);

    @Query(nativeQuery = true, value = """
            SELECT score FROM user_score
            WHERE user_id = :userId
            """)
    Optional<Double> findScoreByUserId(Long userId);

    default Double getScoreByUserId(Long userId) {
        return findScoreByUserId(userId).orElse(0.0);
    }

    @Query(nativeQuery = true, value = """
            SELECT user_id, score FROM user_score
            WHERE user_id IN :userIds
            """)
    List<UserScoreProjection> findScoreByUserIds(@Param("userIds") List<Long> userIds);

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO user_score (user_id, score)
            VALUES (:userId, :earnedScore)
            ON CONFLICT (user_id)
            DO UPDATE SET score = user_score.score + :earnedScore
            """)
    void upsertScore(@Param("userId") Long userId,
                     @Param("earnedScore") Double score);

    @Query(nativeQuery = true,
            value = """
                    SELECT
                        us.user_id as userId,
                        us.score as score
                    FROM user_score us
                    ORDER BY score DESC
                    """,
            countQuery = "SELECT COUNT(*) FROM user_score"
    )
    Page<UserScoreProjection> findTopScores(Pageable pageable);
}