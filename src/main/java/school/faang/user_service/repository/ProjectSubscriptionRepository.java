package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.ProjectSubscription;

import java.util.List;

public interface ProjectSubscriptionRepository extends JpaRepository<ProjectSubscription, Long> {
    @Query(nativeQuery = true, value = """
            SELECT EXISTS (
                SELECT 1 FROM project_subscription 
                WHERE follower_id = :followerId 
                AND project_id = :projectId
            )
            """)
    boolean existsByFollowerIdAndProjectId(long followerId, long projectId);

    @Query(nativeQuery = true, value = """
            INSERT INTO project_subscription (follower_id, project_id) VALUES (:followerId, :projectId)
            """)
    @Modifying
    void followProject(long followerId, long projectId);

    @Query(nativeQuery = true, value = """
            DELETE FROM project_subscription WHERE follower_id = :followerId AND project_id = :projectId
            """)
    @Modifying
    void unfollowProject(long followerId, long projectId);

    @Query(nativeQuery = true, value = "SELECT COUNT(id) FROM project_subscription WHERE project_id = :projectId")
    int findFollowersAmountByProjectId(long projectId);

    @Query(nativeQuery = true, value = "SELECT project_id FROM project_subscription WHERE follower_id = :followerId")
    List<Long> getByFollowerId(long followerId);
}