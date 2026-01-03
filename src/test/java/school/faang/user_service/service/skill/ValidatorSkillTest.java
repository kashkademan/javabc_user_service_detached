package school.faang.user_service.service.skill;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidatorSkillTest {

    @InjectMocks
    private SkillValidator skillValidator;
    @Mock
    private SkillRepository skillRepository;

    @Test
    public void testTitleExistence() {
        when(skillRepository.existsByTitle(prepareDataSkillDto().getTitle())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> skillValidator.validatorTitleSkill(prepareDataSkillDto()));
    }

    @Test
    public void testThrowsCheckValidRecommendation() {
        assertThrows(DataValidationException.class, () -> skillValidator.checkValidRecommendation(getSkillOffers(2)));
    }

    @Test
    public void testDoesThrowsCheckValidRecommendation() {
        assertDoesNotThrow(() -> skillValidator.checkValidRecommendation(getSkillOffers(3)));
    }

    @Test
    public void testEmptySkillOffer() {
        assertThrows(DataValidationException.class, () -> skillValidator.validatorSkillOfferIsEmpty(List.of()));
    }

    @Test
    public void testDoesEmptySkillOffer() {
        assertDoesNotThrow(() -> skillValidator.validatorSkillOfferIsEmpty(getSkillOffers(1)));
    }


    private @NotNull SkillDto prepareDataSkillDto() {
        return new SkillDto(2L, "test");
    }

    private @NotNull List<SkillOffer> getSkillOffers(int ListSize) {
        List<SkillOffer> result = new ArrayList<>();

        for (int i = 0; i < ListSize; i++) {
            result.add(
                    SkillOffer.builder()
                            .id(1L + i)
                            .skill(Skill.builder()
                                    .id(1L)
                                    .title("Fly")
                                    .build())
                            .recommendation(Recommendation.builder()
                                    .id(1L + i)
                                    .author(User.builder()
                                            .id(2L + i)
                                            .build())
                                    .receiver(User.builder()
                                            .id(1L)
                                            .build())
                                    .build())
                            .build());
        }
        return result;
    }
}