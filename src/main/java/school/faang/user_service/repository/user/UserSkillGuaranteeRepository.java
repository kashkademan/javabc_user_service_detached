package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.user.UserSkillGuarantee;

import java.util.List;

public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {
    @Query("SELECT g FROM UserSkillGuarantee g WHERE g.user.id = :userId AND g.skill.id = :skillId")
    List<UserSkillGuarantee> findAllByUserIdAndSkillId(long userId, long skillId);
}