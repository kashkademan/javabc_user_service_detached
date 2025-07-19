package school.faang.user_service.service.skill;

import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.List;

/*

${SkillService} — интерфейс для управления навыками пользователей
<p>
TODO:
 Операции:
 Создание нового навыка.
 Получение всех навыков, которые есть у пользователя.
 Получение навыка, предложенного другими пользователями.
 Присвоение пользователю навыка из предложенных.
</p>*
@author ${JasonRon}
@since ${19.07.2025}*/
public interface SkillService {
    SkillDto create(CreateSkillDto dto);

    List<SkillDto> getByUserId(Long userId);

    List<SkillCandidateDto> getOfferedSkills(long userId);

    void acquireSkillFromOffers(long skillId, long userId);
}
