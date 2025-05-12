package school.faang.user_service.validation.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.skill.SkillAlreadyExistsException;
import school.faang.user_service.exception.skill_offer.NotEnoughSkillOffersException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    private static final String SKILL_TITLE = "Java";
    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillValidator skillValidator;

    @Test
    void testCheckSkillTitleIsNotUnique() {
        when(skillRepository.existsByTitle(anyString())).thenReturn(true);

        assertThrows(SkillAlreadyExistsException.class, () -> skillValidator.checkSkillTitleIsUnique(SKILL_TITLE));
    }

    @Test
    void testCheckSkillTitleIsUnique() {
        when(skillRepository.existsByTitle(anyString())).thenReturn(false);

        assertDoesNotThrow(() -> skillValidator.checkSkillTitleIsUnique(SKILL_TITLE));
    }

    @Test
    void testCheckNotEnoughOffersToAcquireSkill() {
        List<SkillOffer> offers = List.of(new SkillOffer(), new SkillOffer());

        assertThrows(NotEnoughSkillOffersException.class, () -> skillValidator.checkEnoughOffersToAcquireSkill(offers));
    }

    @Test
    void testCheckEnoughOffersToAcquireSkill() {
        List<SkillOffer> offers = List.of(new SkillOffer(), new SkillOffer(), new SkillOffer());

        assertDoesNotThrow(() -> skillValidator.checkEnoughOffersToAcquireSkill(offers));
    }
}
