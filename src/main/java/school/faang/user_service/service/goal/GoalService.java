package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;

import java.util.List;

/**
 * Сервис для управления целями.
 * <p>
 * Отвечает за бизнес-логику создания и последующей работы с целями пользователей.
 */
public interface GoalService {
    /**
     * Создаёт цель на основе переданных данных.
     * <p>
     * Валидационные правила и побочные эффекты:
     * <ul>
     *     <li>Дедлайн должен предоставлять как минимум один полный день от текущего момента;
     *     при нарушении выбрасывается {@code DataValidationException}.</li>
     *     <li>Если указан {@code mentorId}, он должен совпадать с идентификатором текущего пользователя
     *     (из контекста); в противном случае выбрасывается {@code ForbiddenException}.
     *     Кроме того, ментор не может создавать цель сам для себя —
     *     при наличии {@code mentorId} в списке пользователей выбрасывается {@code ForbiddenException}.</li>
     *     <li>Если {@code mentorId} не указан:
     *     размер {@code userIds} должен быть равен 1 и этот идентификатор обязан совпадать с id текущего пользователя;
     *     иначе выбрасываются {@code DataValidationException} либо {@code ForbiddenException}.</li>
     *     <li>Список {@code userIds} должен содержать только уникальные значения;
     *     иначе {@code DataValidationException}.</li>
     *     <li>Для каждого пользователя из {@code userIds} количество активных целей должно быть меньше 2;
     *     при нарушении выбрасывается {@code DataValidationException}.</li>
     *     <li>Если переданы {@code skillIds}:
     *     значения должны быть уникальными; иначе {@code DataValidationException}.
     *     Каждая ссылка на скилл должна существовать; иначе {@code EntityNotFoundException}.</li>
     *     <li>Если передан {@code parentGoalId}, соответствующая цель должна существовать;
     *     иначе {@code EntityNotFoundException}.</li>
     *     <li>При отсутствии ментора текущий пользователь добавляется как единственный участник цели.</li>
     *     <li>При наличии ментора ментор назначается у цели, в участники добавляются пользователи из {@code userIds},
     *     а для каждой пары (пользователь, скилл) создаются гарантии скиллов.</li>
     *     <li>Цель сохраняется со статусом {@code GoalStatus.ACTIVE}.</li>
     * </ul>
     * Возвращаемое значение формируется маппером и содержит основные поля цели, включая
     * {@code title}, {@code description}, {@code deadline}, {@code mentorId}, {@code userIds},
     * {@code status} и {@code parentGoalId}.
     *
     * @param createGoalDto входные данные для создания цели
     * @return {@link GoalDto} с данными созданной цели
     */
    GoalDto create(CreateGoalDto createGoalDto);

    /**
     * Удаляет цель по идентификатору с учётом ролей и состава участников.
     * <p>
     * Правила и побочные эффекты:
     * <ul>
     *     <li>Если цель является родительской (имеет подцели) — выбрасывается {@code ForbiddenException}.</li>
     *     <li>Если у цели нет ментора:
     *         <ul>
     *             <li>Удалить может только участник цели; иначе {@code ForbiddenException}.</li>
     *             <li>Если в цели один участник — цель удаляется целиком ({@code deleteById}).</li>
     *             <li>Если участников больше одного — из цели удаляется только текущий пользователь
     *             ({@code deleteUserFromGoal}).</li>
     *         </ul>
     *     </li>
     *     <li>Если у цели есть ментор:
     *         <ul>
     *             <li>Удалить может только ментор; при несовпадении идентификаторов — {@code ForbiddenException}.</li>
     *             <li>Цель удаляется целиком ({@code deleteById}).</li>
     *         </ul>
     *     </li>
     *     <li>Если у удаляемой цели присутствуют навыки, для каждого навыка удаляются гарантии
     *     ({@code userSkillGuaranteeRepository.deleteBySkillId}).</li>
     * </ul>
     *
     * @param goalId идентификатор цели для удаления
     * @throws school.faang.user_service.exception.EntityNotFoundException если цель не найдена
     * @throws school.faang.user_service.exception.ForbiddenException при нарушении правил доступа/удаления
     */
    void delete(long goalId);

    /**
     * Получает список целей с применением фильтров.
     * <p>
     * Метод выполняет фильтрацию целей по заданным критериям и возвращает результат в виде DTO.
     * Фильтрация происходит в памяти после получения всех целей из репозитория.
     * <p>
     * Поддерживаемые фильтры:
     * <ul>
     *     <li>{@code titleContains} - поиск по частичному совпадению в названии цели (регистрозависимый)</li>
     *     <li>{@code descriptionContains} - поиск по частичному совпадению в описании цели (регистрозависимый)</li>
     *     <li>{@code status} - точное совпадение статуса цели</li>
     *     <li>{@code mentorId} - фильтрация по идентификатору ментора цели</li>
     * </ul>
     * <p>
     * Правила фильтрации:
     * <ul>
     *     <li>Если фильтр равен {@code null}, он игнорируется</li>
     *     <li>Все фильтры применяются одновременно (логическое И)</li>
     *     <li>Поиск по тексту чувствителен к регистру</li>
     *     <li>Для {@code mentorId} проверяется точное совпадение с ментором цели</li>
     *     <li>Если у цели нет ментора, она не пройдет фильтр по {@code mentorId}</li>
     * </ul>
     * <p>
     * Валидация и побочные эффекты:
     * <ul>
     *     <li>Текущий пользователь должен существовать в системе; иначе {@code EntityNotFoundException}</li>
     *     <li>Метод получает все цели из репозитория и применяет фильтры в памяти</li>
     *     <li>Результат преобразуется в DTO с помощью маппера</li>
     * </ul>
     * <p>
     * Примеры использования:
     * <pre>{@code
     * // Поиск всех активных целей
     * GoalFilterDto filters = new GoalFilterDto(null, null, GoalStatus.ACTIVE, null);
     * List<GoalDto> activeGoals = goalService.getByFilters(filters);
     * 
     * // Поиск целей с "Java" в названии и ментором с ID 123
     * GoalFilterDto filters = new GoalFilterDto("Java", null, null, 123L);
     * List<GoalDto> javaGoals = goalService.getByFilters(filters);
     * }</pre>
     *
     * @param filters объект с критериями фильтрации
     * @return список целей, соответствующих заданным фильтрам
     * @throws school.faang.user_service.exception.EntityNotFoundException если текущий пользователь не найден в системе
     */
    List<GoalDto> getByFilters(GoalFilterDto filters);
}
