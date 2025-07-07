package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.mentorship.Mentorship;

public interface MentorshipRelationRepository extends JpaRepository<Mentorship, Long> {
    boolean existsByMentorIdAndMenteeId(long mentorId, long menteeId);


}
