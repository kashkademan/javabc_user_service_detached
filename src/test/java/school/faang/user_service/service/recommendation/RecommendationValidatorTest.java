package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationValidatorTest {

    @InjectMocks
    private RecommendationValidator recommendationValidator;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    private static final List<Long> SKILL_ID_LIST = List.of(1L, 2L, 3L, 3L);

    @Test
    public void testValidateRecommendationDate() {
        LocalDateTime thresholdDate = LocalDateTime.of(2024, 8, 9, 15, 15, 15);
        LocalDateTime recommendationDate = LocalDateTime.of(2024, 9, 2, 15, 15, 15);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validateRecommendationDate(recommendationDate, thresholdDate));
    }

    @Test
    public void testExceptionAuthorSkillGuaranteed() {
        Long guarantorId = 1L;
        Long userId = 2L;
        List<UserSkillGuarantee> userSkillGuaranteeList = List.of(UserSkillGuarantee.builder()
                .id(1L)
                .build());

        when(userSkillGuaranteeRepository.findBySkillIdInAndGuarantorIdAndUserId(SKILL_ID_LIST, guarantorId, userId)).thenReturn(userSkillGuaranteeList);
        assertThrows(DataValidationException.class, () -> recommendationValidator.validatorExistenceUserSkillGuarantee(prepareDataRecommendationDto(), SKILL_ID_LIST));
    }

    @Test
    public void presenceDuplicates() {
        assertThrows(DataValidationException.class, () -> recommendationValidator.validatorIdDuplicates(SKILL_ID_LIST));
    }

    @Test
    public void ExistenceRecommendation() {
        long id = 1L;
        when(recommendationRepository.existsById(id)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationValidator.validatorExistenceRecommendation(id));
    }

    private RecommendationDto prepareDataRecommendationDto() {
        return RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .build();
    }
}