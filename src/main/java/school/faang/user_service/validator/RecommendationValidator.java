package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationRepository recommendationRepository;

    public void validateRecommendationDate(LocalDateTime recommendationDate, LocalDateTime thresholdDate) {
        if (thresholdDate.isBefore(recommendationDate)) {
            Duration diff = Duration.between(recommendationDate, thresholdDate);
            throw new DataValidationException(" Нельзя создать рекомендацию раньше 6 месяцев с последней рекомендации пользователю прошло "
                    + diff.toDaysPart() + " д. " + diff.toHoursPart() + " ч. " + diff.toMinutesPart() + " мин.");
        }
    }

    public void validatorExistenceUserSkillGuarantee(RecommendationDto recommendation, List<Long> skillIds) {
        List<UserSkillGuarantee> userSkillGuaranteeList = userSkillGuaranteeRepository.findBySkillIdInAndGuarantorIdAndUserId(
                skillIds, recommendation.getAuthorId(), recommendation.getReceiverId());
        if (!userSkillGuaranteeList.isEmpty()) {
            String skillTitle = userSkillGuaranteeList.stream()
                    .map(UserSkillGuarantee::getSkill)
                    .filter(Objects::nonNull)
                    .map(Skill::getTitle)
                    .collect(Collectors.joining(", "));

            throw new DataValidationException("Вы уже являетесь гарантом навыков: " + skillTitle);
        }
    }

    public void validatorIdDuplicates(List<Long> skillId) {
        Set<Long> setId = new HashSet<>(skillId);
        if (skillId.size() != setId.size()) {
            throw new DataValidationException("В рекомендации не может быть несколько предложений одного и того же навыка!");
        }
    }

    public void validatorExistenceRecommendation(long id) {
        if (!recommendationRepository.existsById(id)) {
            throw new DataValidationException("Такой рекомендации не существует!");
        }
    }

    public void validatorNullSKillOfferTru(List<SkillOfferDto> skillOffers){
        if (skillOffers != null && skillOffers.isEmpty()){
            throw new DataValidationException("В рекомендации нет skillOffer!");
        }
    }
}