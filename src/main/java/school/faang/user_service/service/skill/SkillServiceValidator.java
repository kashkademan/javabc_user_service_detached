package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SkillServiceValidator {
    private final SkillRepository repository;
    private final SkillOfferRepository offerRepository;

    public void validateNotNull(Object object, String string) {
        if (object == null) {
            throw new DataValidationException(string);
        }
    }

    public void validationByNameSkillInTheDataBase(String title) {
        if (repository.existsByTitle(title)) {
            throw new EntityNotFoundException("Навык '%s' уже существует в базе".formatted(title));
        }
    }

    public void validationCountOfferOfSkill(Long skillId, Long userId, Integer countRecommendation) {
        if (offerRepository.countAllOffersOfSkill(skillId, userId) < countRecommendation) {
            throw new ForbiddenException("Недостаточное колличество рекоммендаций навыка, добавление невозможно");
        }
    }

    public void validationSkillOfUser(Long skillId, Long userId) {
        Optional<Skill> optional = repository.findUserSkill(skillId, userId);
        if (optional.isPresent()) {
            throw new ForbiddenException("Навык '%s' уже есть у пользователя".formatted(optional));
        }
    }
}
