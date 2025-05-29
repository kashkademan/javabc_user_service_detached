package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.User;

public interface MentorshipRepository extends JpaRepository<User, Long> {
}
