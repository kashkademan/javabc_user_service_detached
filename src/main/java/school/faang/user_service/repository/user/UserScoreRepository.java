package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.user.UserScore;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScore, Long> {
}
