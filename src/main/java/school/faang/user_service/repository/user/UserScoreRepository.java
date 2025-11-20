package school.faang.user_service.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.user.UserScoreEvent;

import java.util.UUID;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScoreEvent, UUID> {

    @Query(value = """
            SELECT use.user_id as userId, CAST(SUM(use.points) AS INTEGER) as points
            FROM user_score_event use
            GROUP BY use.user_id
            ORDER BY points DESC
            """, nativeQuery = true)
    Page<UserPointsProjection> getLeaderBoard(Pageable pageable);

    interface UserPointsProjection {
        Long getUserId();
        Integer getPoints();
    }
}
