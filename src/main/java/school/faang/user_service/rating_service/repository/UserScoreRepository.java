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
    void save(@Param("userId") Long userId, @Param("score") int score);


    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE user_score
            SET score = score + :increment
            WHERE user_id = :userId
            """)
    void setIncrement(@Param("userId") Long userId,
                      @Param("increment") int increment);

    @Query(nativeQuery = true, value = """
            SELECT score FROM user_score
            WHERE user_id = :userId
            """)
    Integer findScoreByUserId(Long userId);
}













