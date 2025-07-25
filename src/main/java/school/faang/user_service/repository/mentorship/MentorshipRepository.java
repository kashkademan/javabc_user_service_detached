package school.faang.user_service.repository.mentorship;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;

public interface MentorshipRepository extends JpaRepository<User, Long> {

    default User getByIdOrThrow(long userId) {
        return findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %d not found", userId)));
    }

    @Query(value = "SELECT COUNT(*) > 0 FROM mentorship "
                   + "WHERE mentor_id = :mentorId "
                   + "AND mentee_id = :menteeId", nativeQuery = true)
    boolean existsByMentorIdAndMenteeId(Long mentorId, Long menteeId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO mentorship (mentor_id, mentee_id) "
                   + "VALUES (:mentorId, :menteeId)", nativeQuery = true)
    void createMentorship(Long mentorId, Long menteeId);
}