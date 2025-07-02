package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillOfferServiceTest {
    private static final long SKILL_ID = 1L;
    private static final long SKILL_OFFER_ID = 1L;
    private static final long USER_ID = 1L;

    private static SkillOffer skillOffer;
    private static List<SkillOffer> skillOffers;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private SkillOfferService skillOfferService;

    @BeforeAll
    static void init() {
        skillOffer = new SkillOffer();
        skillOffer.setId(SKILL_OFFER_ID);
        skillOffers = new ArrayList<>();
        skillOffers.add(skillOffer);
    }

    @Test
    void findAllOffersOfSkill() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(skillOfferRepository.findAllOffersOfSkill(anyLong(), anyLong())).thenReturn(skillOffers);

        List<SkillOffer> userOffersOfSkill = skillOfferService.findAllOffersOfSkill(SKILL_ID);

        assertEquals(skillOffers, userOffersOfSkill);
    }
}
