package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.user.UserScore;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO user_score(user_id, score)
            VALUES (:userId, :delta)
            ON CONFLICT (user_id)
            DO UPDATE SET score = user_score.score + :delta
            RETURNING score
            """)
    int upsertAndIncrementScore(@Param("userId") long userId, @Param("delta") int delta);
}
