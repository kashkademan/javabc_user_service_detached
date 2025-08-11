package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import school.faang.user_service.entity.Subscription;
import school.faang.user_service.entity.FolloweeSumProjection;

@Repository
public interface SubscriptionEntityRepository extends JpaRepository<Subscription, Long> {
    @Query("""
        SELECT s.followeeId AS followeeId, COUNT(s.followerId) AS countFollower
        FROM Subscription s
        GROUP BY s.followeeId
        ORDER BY COUNT(s.followerId) DESC
        """)
    Page<FolloweeSumProjection> findFolloweesBySumOfFollowers(Pageable pageable);

    @Query("""
        SELECT DISTINCT s.followerId
        FROM Subscription s
        """)
    Page<Long> findDistinctFollowerIds(Pageable pageable);
}
