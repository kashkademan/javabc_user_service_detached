package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface RecommendationRequestRepository extends JpaRepository<RecommendationRequest, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM recommendation_request
            WHERE requester_id = ?1 AND receiver_id = ?2
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<RecommendationRequest> findLatestPendingRequest(long requesterId, long receiverId);

    @EntityGraph(attributePaths = {"requester", "receiver"})
    @Query(
            value = """
                    SELECT r FROM RecommendationRequest r
                    WHERE r.requester.id = :requesterId AND r.receiver.id = :receiverId
                    ORDER BY r.createdAt DESC
                    """)
    Optional<RecommendationRequest> findLatestRequest(@Param("requesterId") long requesterId, @Param("receiverId") long receiverId);

    @EntityGraph(attributePaths = {"requester", "receiver"})
    @Override
    Optional<RecommendationRequest> findById(Long id);

    @EntityGraph(attributePaths = {"requester", "receiver"})
    @Query(
            value = """
                    select r from RecommendationRequest r
                    join r.requester requester
                    join r.receiver receiver
                    where (:requesterId is null or requester.id = :requesterId)
                        and (:receiverId is null or receiver.id = :receiverId)
                        and (:messageContains is null or lower(r.message) like lower(concat('%', :messageContains, '%')))
                        and (:status is null or r.status = :status)
                    """
    )
    List<RecommendationRequest> findByFilters(
            @Param("requesterId") Long requesterId,
            @Param("receiverId") Long receiverId,
            @Param("messageContains") String messageContains,
            @Param("status") RequestStatus status
    );

    default RecommendationRequest getByIdOrThrow(long requestId) {
        return findById(requestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Recommendation request %d not found", requestId))
        );
    }
}
