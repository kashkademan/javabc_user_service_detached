package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Optional;

public interface RecommendationRequestRepository extends JpaRepository<RecommendationRequest, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM recommendation_request
            WHERE requester_id = ?1 AND receiver_id = ?2 AND status = 1
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<RecommendationRequest> findLatestPendingRequest(long requesterId, long receiverId);

    @Query(nativeQuery = true, value = """
            select count(*) from recommendation_request rr
             where rr.requester_id = :requesterId
               and rr.receiver_id = :receiverId
               and rr.status in (0, 1)
               and rr.created_at + interval '6 MONTH' > now()
            """)
    int countRepeatedRequest(long requesterId, long receiverId);

    @Query("update RecommendationRequest r set r.status = :requestStatus, r.rejectionReason = :reason where r.id = :id")
    @Modifying
    Integer setStatus(Long id, RequestStatus requestStatus, String reason);
}