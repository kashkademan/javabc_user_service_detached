package school.faang.user_service.repository.skill;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.SkillRepositoryCustom;

import java.util.List;

@Repository
public class SkillRepositoryCustomImpl implements SkillRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    @Modifying
    public void assignSkillsToUserBatch(List<Long> skillIds, Long userId) {
        if (skillIds == null || skillIds.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO user_skill (skill_id, user_id) VALUES ");

        for (int i = 0; i < skillIds.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("(?, ?)");
        }

        Query query = entityManager.createNativeQuery(sql.toString());

        int paramIndex = 1;
        for (Long skillId : skillIds) {
            query.setParameter(paramIndex++, skillId);
            query.setParameter(paramIndex++, userId);
        }

        query.executeUpdate();
    }
}
