package school.faang.user_service.service.skill;

import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillViewDto;

import java.util.List;

/**
 * Интерфейс сервиса для управления навыками пользователей.
 * <p>
 * Определяет основные операции для работы с навыками:
 * <ul>
 *   <li>Создание новых навыков в системе</li>
 *   <li>Получение навыков, принадлежащих пользователю</li>
 *   <li>Работа с навыками, предложенными другими пользователями</li>
 *   <li>Подтверждение/принятие предложенных навыков</li>
 * </ul>
 *
 * @author JasonRon
 * @apiNote Все методы должны быть реализованы как thread-safe
 * @see SkillServiceImpl
 * @since 19.07.2025
 */
public interface SkillService {
    SkillViewDto create(SkillCreateDto dto);

    List<SkillViewDto> getByUserId(Long userId);

    List<SkillCandidateDto> getOfferedSkills(Long userId);

    void acquireSkillFromOffers(Long skillId, Long userId);
}
