package school.faang.user_service.repository.skill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

public interface SkillOfferRepository extends JpaRepository {

    @Query("SELECT so FROM SkillOffer so WHERE so.offeredUser.id = :userId")
    List<SkillOffer> findSkillsOfferedToUser(@Param("userId") long userId);

    @Query("SELECT COUNT(so) FROM SkillOffer so WHERE so.skill.id = :skillId AND so.offeredUser.id = :userId")
    int countAllOffersOfSkill(@Param("skillId") long skillId, @Param("userId") long userId);

    List<SkillOffer> findAllBySkillIdAndOfferedUserId(long skillId, long userId);

}
