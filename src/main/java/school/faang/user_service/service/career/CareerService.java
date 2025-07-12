package school.faang.user_service.service.career;

import school.faang.user_service.dto.career.CareerViewDto;
import school.faang.user_service.dto.career.CareerCreateDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

/**
 * Сервис для управления карьерной информацией пользователей.
 * <p>
 * Предоставляет методы для добавления, обновления и получения записей о карьере.
 *
 * @author fomchenkoandrey
 */
public interface CareerService {

    /**
     * Создаёт новую запись о карьере для указанного пользователя.
     * <p>
     * Условия:
     * <ul>
     *     <li>Дата начала карьеры {@code from} не может быть в будущем — иначе выбрасывается
     *     {@link DataValidationException}.</li>
     *     <li>Пользователь должен существовать — иначе выбрасывается {@code EntityNotFoundException}.</li>
     * </ul>
     *
     * @param userId    идентификатор пользователя, добавляющего карьеру
     * @param careerDto объект {@link CareerViewDto}, содержащий данные для создания записи
     * @return объект {@link CareerViewDto}, представляющий созданную запись
     */
    CareerViewDto career(long userId, CareerCreateDto careerDto);

    /**
     * Обновляет существующую запись о карьере пользователя.
     * <p>
     * Условия:
     * <ul>
     *     <li>Дата начала карьеры {@code from} не может быть в будущем — иначе выбрасывается
     *     {@link DataValidationException}.</li>
     *     <li>Обновление разрешено только владельцу записи — иначе выбрасывается
     *     {@link ForbiddenException}.</li>
     *     <li>Запись должна существовать — иначе выбрасывается {@code EntityNotFoundException}.</li>
     * </ul>
     *
     * @param userId    идентификатор пользователя, выполняющего обновление
     * @param careerId  идентификатор обновляемой записи
     * @param careerDto объект {@link CareerViewDto}, содержащий обновлённые данные
     * @return объект {@link CareerViewDto}, представляющий обновлённую запись
     */
    CareerViewDto updateCareer(long userId, long careerId, UpdateCareerDto careerDto);

    /**
     * Возвращает запись о карьере по её идентификатору.
     * <p>
     * Условия:
     * <ul>
     *     <li>Если запись не найдена — выбрасывается {@code EntityNotFoundException}.</li>
     * </ul>
     *
     * @param careerId идентификатор карьерной записи
     * @return объект {@link CareerViewDto}, представляющий найденную запись
     */
    CareerViewDto getById(long careerId);
}
