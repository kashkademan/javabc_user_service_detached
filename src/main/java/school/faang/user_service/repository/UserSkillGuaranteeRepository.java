package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.UserSkillGuarantee;

public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {
}