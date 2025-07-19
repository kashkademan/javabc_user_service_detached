package school.faang.user_service.dto.skill;

/*

${CreateSkillDto} — неизменяемая структура данных (record), предназначенная для создания нового навыка.
<p>
TODO: Используется при получении данных от клиента, содержащих название нового навыка,
 который нужно добавить в систему
</p>
@param title Название создаваемого навыка.
@author ${JasonRon}
@since ${19.07.2025}*/
public record CreateSkillDto(String title) {
}
