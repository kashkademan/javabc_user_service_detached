package school.faang.user_service.dto.skill;

import school.faang.user_service.dto.user.UserDto;

import java.util.List;
/*

${SkillDto} — неизменяемая структура данных (record), представляющая навык пользователя.
<p>
TODO: Используется для передачи информации о навыке, включая его идентификатор, название
 и список пользователей-гарантов (тех, кто подтвердил или рекомендовал этот навык).
</p>
@param id уникальный идентификатор навыка.
@param title название навыка.
@param guarantors Список пользователей, выступающие гаранторами навыка.
@author ${JasonRon}
@since ${19.07.2025}*/

public record SkillDto(
        Long id,
        String title,
        List<UserDto> guarantors
) {
}
