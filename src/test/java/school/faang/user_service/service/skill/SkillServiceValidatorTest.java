package school.faang.user_service.service.skill;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Проверка валидатора данных для сервиса")
public class SkillServiceValidatorTest {
    @Mock
    private SkillRepository repository;
    @Mock
    private SkillOfferRepository offerRepository;
    @InjectMocks
    private SkillServiceValidator validator;

    private final int countOperations = 1;
    private long skillId = 1L;
    private long userId = 2L;
    private int countRecommendation = 3;
    private int returnCountRecommendation = 2;
    private String skillTitle = "title";

    @Test
    @DisplayName("Проверка возникновения ошибки при наличии навыка с указанным именем в базе данных ")
    public void testValidationByNameSkillInTheDataBase() {
        Mockito.when(repository.existsByTitle(skillTitle)).thenReturn(true);

        assertThrows(EntityNotFoundException.class, () -> validator.validationByNameSkillInTheDataBase(skillTitle));
        verify(repository, times(countOperations)).existsByTitle(skillTitle);
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при недостаточном количестве рекомендаций навыка ")
    public void testValidationCountOfferOfSkill() {
        Mockito.when(offerRepository.countAllOffersOfSkill(skillId, userId))
                .thenReturn(returnCountRecommendation);

        assertThrows(ForbiddenException.class,
                () -> validator.validationCountOfferOfSkill(skillId, userId, countRecommendation));
        verify(offerRepository, times(countOperations)).countAllOffersOfSkill(skillId, userId);
    }

    @Test
    @DisplayName("Проверка возникновения ошибки при обнаружении данного навыка у пользователя ")
    public void testValidationSkillOfUser() {
        Skill skill = new Skill();
        Optional<Skill> optional = Optional.of(skill);
        Mockito.when(repository.findUserSkill(skillId, userId))
                .thenReturn(optional);

        assertThrows(ForbiddenException.class, () -> validator.validationSkillOfUser(skillId, userId));
        verify(repository, times(countOperations)).findUserSkill(skillId, userId);
    }
}
