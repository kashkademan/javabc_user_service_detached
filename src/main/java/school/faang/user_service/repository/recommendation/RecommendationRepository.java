package school.faang.user_service.repository.recommendation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO recommendation (author_id, receiver_id, content)
            VALUES (?1, ?2, ?3) RETURNING id
            """)
    @Modifying
    Long create(Long authorId, Long receiverId, String content);

    @Query(nativeQuery = true, value = """
            UPDATE recommendation SET content = :content
            WHERE author_id = :authorId AND receiver_id = :receiverId
            """)
    @Modifying
    void update(long authorId, long receiverId, String content);

    @Modifying
    int deleteByIdAndAuthor_id(Long id, Long authorId);

    List<Recommendation> findAllByReceiverId(long receiverId);

    List<Recommendation> findAllByAuthorId(long authorId);

    Optional<Recommendation> findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(Long authorId, Long receiverId);

    @EntityGraph(attributePaths = {"author", "receiver"})
    Optional<Recommendation> findById(Long id);

    Optional<Long> findAuthorIdById(Long id);

    @EntityGraph(attributePaths = {"author", "receiver"})
    @Query(
            value = """
                    select r from Recommendation r
                    join r.author author
                    join r.receiver receiver
                    where (:contentContains is null or lower(r.content) like lower(concat('%', :contentContains, '%')))
                        and (:receiverId is null or receiver.id = :receiverId)
                        and (:authorId is null or author.id = :authorId)
                    """,
            countQuery = """
                    select count(r) from Recommendation r
                    join r.author author
                    join r.receiver receiver
                    where (:contentContains is null or lower(r.content) like lower(concat('%', :contentContains, '%')))
                        and (:receiverId is null or receiver.id = :receiverId)
                        and (:authorId is null or author.id = :authorId)
                    """
    )
    Page<Recommendation> findByFilters(
            @Param("contentContains") String contentContains,
            @Param("receiverId") Long receiverId,
            @Param("authorId") Long authorId,
            Pageable pageable
    );
}
