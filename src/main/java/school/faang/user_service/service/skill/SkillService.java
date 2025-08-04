package school.faang.user_service.service.skill;

import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.dto.skill.SkillViewDto;

import java.util.List;

/**
 * Интерфейс предоставляющий операции для
 * <ul>
 *  <li> создание скилла </li>
 *  <li> получение списка скиллов пользователя </li>
 *  <li> просмотр предложенных скиллов от других пользователей </li>
 *  <li> приобретение предложенных скиллов </li>
 * </ul>
 * <p>
 *
 * @author Dmitry B.
 * @since 14.07.2025
 */

public interface SkillService {

    /**
     * Создает новый скилл.
     * <p>
     *
     * @param skillDto данные для создания скила
     * @return созданный скилл в виде {@link SkillViewDto}
     */

    SkillViewDto create(SkillCreateDto skillDto);

    /**
     * Возвращает лист скилов пользователя по его ID
     * <p>
     *
     * @param userId данные для поиска по ID
     * @return набор скилов пользователя в виде листа из {@link SkillViewDto}
     */

    List<SkillViewDto> getByUserId(Long userId);

    /**
     * Возвращает лист рекомендованных другими пользователями скилов пользователю по его ID
     * <p>
     *
     * @return набор рекомендованных скилов пользователя в виде листа из {@link SkillOfferDto}
     */

    List<SkillOfferDto> getOfferedSkills();

    /**
     * Добавление скила пользователю по ID скилла и ID пользователя
     * <p>
     *
     * @param skillId ID добавляемого скила
     */

    void acquireSkillFromOffers(Long skillId);
}
