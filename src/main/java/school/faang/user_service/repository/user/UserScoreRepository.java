package school.faang.user_service.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.dto.user.LeaderScoreDto;
import school.faang.user_service.entity.user.UserScoreEvent;

import java.util.UUID;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScoreEvent, UUID> {

    @Query("""
            SELECT new school.faang.user_service.dto.user.LeaderScoreDto(
                e.username,
                CAST(SUM(e.points) AS INTEGER)
            )
            FROM UserScoreEvent e
            GROUP BY e.username
            ORDER BY SUM(e.points) DESC
            """)
    Page<LeaderScoreDto> getLeaderBoard(Pageable pageable);
}
