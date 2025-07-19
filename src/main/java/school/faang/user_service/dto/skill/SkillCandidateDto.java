package school.faang.user_service.dto.skill;

/*

${SkillCandidateDto} — неизменяемая структура данных (record), представляющая навык,
предложенный пользователю другими пользователями.
<p>
TODO: Используется для отображения навыков, которые другие пользователи рекомендуют
 или подтверждают для текущего пользователя, а также количества таких предложений.
</p>
@param skill dto-объект навыка, который был предложен.
@param offersAmount Количество пользователей, предложивших навык.
@author ${JasonRon}
@since ${19.07.2025}*/
public record SkillCandidateDto(
        SkillDto skill,
        int offersAmount
) {
}
