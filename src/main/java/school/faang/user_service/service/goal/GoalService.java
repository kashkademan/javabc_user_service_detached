package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;

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
     *     <li>Список {@code userIds} должен содержать только уникальные значения; иначе {@code DataValidationException}.</li>
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
}
