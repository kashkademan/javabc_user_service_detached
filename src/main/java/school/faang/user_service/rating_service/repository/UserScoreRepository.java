package school.faang.user_service.rating_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.rating_service.entity.UserScore;

/**
 * UserScoreRepository — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 05.09.2025
 */
public interface UserScoreRepository extends JpaRepository<UserScore, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO user_score (user_id, score)
            VALUES (:userId, :score)
            """)
    void save(@Param("userId") Long userId,
              @Param("score") Double score);


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
    Double findScoreByUserId(Long userId);

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO user_score (user_id, score)
            VALUES (:userId, :earnedScore)
            ON CONFLICT (user_id)
            DO UPDATE SET score = user_score.score + :earnedScore
            """)
    void upsertScore(@Param("userId") Long userId,
                     @Param("earnedScore") Double score);
}













