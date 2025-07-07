package school.faang.user_service.service.workschedule;

import school.faang.user_service.dto.workschedule.WorkScheduleCreateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleViewDto;

/**
 * Сервис для управления рабочими графиками пользователей.
 * <p>
 * Предоставляет методы для создания, обновления и получения данных о рабочем графике.
 * Все операции проходят проверку бизнес-ограничений и прав доступа.
 * </p>
 *
 * <ul>
 *   <li>Создание рабочего графика</li>
 *   <li>Обновление существующего графика</li>
 *   <li>Получение информации о графике по идентификатору</li>
 * </ul>
 *
 * <p>В случае нарушения бизнес-логики (например, некорректные временные интервалы
 * или попытка изменить чужой график) выбрасываются соответствующие исключения.</p>
 *
 * @author agent
 * @since 07.07.2025
 */
public interface WorkScheduleService {

    /**
     * Создаёт рабочий график для указанного пользователя.
     * <p>
     * График должен соответствовать правилам: начало рабочего дня — до его окончания,
     * обед — внутри рабочего времени, с корректным порядком времён.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @param dto    данные нового графика
     * @return созданный график в виде {@link WorkScheduleViewDto}
     *
     * @throws school.faang.user_service.exception.DataValidationException если график некорректен
     * @throws school.faang.user_service.exception.EntityNotFoundException если пользователь не найден
     */
    WorkScheduleViewDto addWorkSchedule(long userId, WorkScheduleCreateDto dto);

    /**
     * Обновляет рабочий график пользователя.
     * <p>
     * Только владелец графика может его обновить. Все временные значения проходят валидацию.
     * </p>
     *
     * @param userId          идентификатор текущего пользователя
     * @param workScheduleId  идентификатор обновляемого графика
     * @param dto             новые значения графика
     * @return обновлённый график в виде {@link WorkScheduleViewDto}
     *
     * @throws school.faang.user_service.exception.ForbiddenException если доступ запрещён
     * @throws school.faang.user_service.exception.DataValidationException если данные некорректны
     * @throws school.faang.user_service.exception.EntityNotFoundException если график или пользователь не найдены
     */
    WorkScheduleViewDto updateWorkSchedule(long userId, long workScheduleId, WorkScheduleUpdateDto dto);

    /**
     * Возвращает рабочий график по идентификатору.
     * <p>
     * Получить данные может только владелец графика.
     * </p>
     *
     * @param workScheduleId идентификатор графика
     * @return найденный график в виде {@link WorkScheduleViewDto}
     *
     * @throws school.faang.user_service.exception.ForbiddenException если текущий пользователь не является владельцем
     * @throws school.faang.user_service.exception.EntityNotFoundException если график не найден
     */
    WorkScheduleViewDto getById(long workScheduleId);

    /**
     * Удаляет рабочий график по идентификатору.
     * <p>
     * Удалить данные может только владелец графика.
     * </p>
     *
     * @param workScheduleId идентификатор графика
     * @return найденный график в виде {@link WorkScheduleViewDto}
     *
     * @throws school.faang.user_service.exception.ForbiddenException если текущий пользователь не является владельцем
     * @throws school.faang.user_service.exception.EntityNotFoundException если график не найден
     */
    void deleteWorkSchedule(long workScheduleId);

}
