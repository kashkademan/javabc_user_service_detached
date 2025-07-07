package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.mentorship.Mentorship;

public interface MentorshipJpaRepository extends JpaRepository<Mentorship, Long> {

    @Query("SELECT COUNT(m) > 0 FROM Mentorship m WHERE m.mentor.id = :mentorId AND m.mentee.id = :menteeId")
    boolean existsByMentorAndMentee(@Param("mentorId") Long mentorId, @Param("menteeId") Long menteeId);
}