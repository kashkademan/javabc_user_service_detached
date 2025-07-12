package school.faang.user_service.service.skill;

import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.Optional;

public class SkillServiceValidator {
    SkillRepository skillRepository;
    SkillOfferRepository skillOfferRepository;


    public void validateNotNull(Object object, String string) {
        if (object == null) {
            throw new DataValidationException(string);
        }
    }

    public void validationByNameSkillInTheDataBase(String title) {
        if (skillRepository.existsByTitle(title)) {
            throw new EntityNotFoundException("Навык '%s' уже существует в базе".formatted(title));
        }
    }

    public void validationCountOfferOfSkill(long skillId, long userId, int countRecommendation) {
        if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) < countRecommendation) {
            throw new ForbiddenException("Недостаточное колличество рекоммендаций навыка, добавление невозможно");
        }
    }

    public void validationSkillOfUser(long skillId, long userId) {
        Optional<Skill> optional = skillRepository.findUserSkill(skillId, userId);
        if (optional.isPresent()) {
            throw new ForbiddenException("Навык '%s' уже есть у пользователя".formatted(optional));
        }
    }
}
