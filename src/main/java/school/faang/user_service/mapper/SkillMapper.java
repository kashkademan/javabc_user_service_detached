package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillViewDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

/**
 * Маппер для преобразования между сущностью Skill и соответствующими DTO.
 * <p>
 * Реализация генерируется автоматически с помощью MapStruct во время компиляции.
 * Содержит следующие основные методы преобразования:
 * <ul>
 *   <li>{@code toSkill} - преобразует DTO в сущность (для сохранения в БД)</li>
 *   <li>{@code toSkillDto} - преобразует сущность в DTO (для передачи клиенту)</li>
 *   <li>{@code toSkillCandidateDto} - преобразует сущность в DTO предложенного навыка</li>
 * </ul>
 *
 * @author JasonRon
 * @apiNote Реализация маппера генерируется автоматически MapStruct
 * @see Skill
 * @see SkillViewDto
 * @since 19.07.2025
 */

@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill toSkill(SkillCreateDto skillDto);

    SkillViewDto toSkillDto(Skill skill);

    default SkillCandidateDto toSkillCandidateDto(Skill skill, Long userId,
                                                  SkillOfferRepository offerRepository) {
        SkillViewDto skillViewDto = toSkillDto(skill);
        int offers = offerRepository.countAllOffersOfSkill(skill.getId(), userId);
        return new SkillCandidateDto(skillViewDto, offers);
    }
}
