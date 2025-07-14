package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.mentorshp.Mentorship;

import java.util.List;

public interface MentorshipRepository extends JpaRepository<Mentorship, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO mentorship (mentor_id, mentee_id, created_at, updated_at)
            VALUES (?1, ?2, NOW(), NOW())
            returning *
            """)
    Mentorship create(long mentorId, long menteeId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM mentorship WHERE mentor_id = ?
            """)
    List<Mentorship> findMentorshipsByMentorId(long mentorId);
}
