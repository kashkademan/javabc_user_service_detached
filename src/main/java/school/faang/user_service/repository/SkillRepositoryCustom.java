package school.faang.user_service.repository;

import java.util.List;

public interface SkillRepositoryCustom {
    void assignSkillsToUserBatch(List<Long> skillIds, Long userId);
}
