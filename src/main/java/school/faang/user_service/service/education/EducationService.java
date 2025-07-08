package school.faang.user_service.service.education;

import school.faang.user_service.dto.user.EducationViewDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

/**
 * Сервис для управления образованием пользователей.
 * <p>
 * Предоставляет методы для создания, обновления и получения данных о образовании пользователя.
 * Все операции проходят проверку бизнес-ограничений и прав доступа.
 * </p>
 *
 * <ul>
 *   <li>Создание образования</li>
 *   <li>Обновление существующего образования</li>
 *   <li>Получение информации о образовании по идентификатору</li>
 * </ul>
 *
 * @author fomchenkoandrey
 */
public interface EducationService {

    /**
     * Создаёт новую запись об образовании для указанного пользователя.
     * <p>
     * Условия:
     * <ul>
     *     <li>Дата начала обучения {@code from} не может быть в будущем — иначе выбрасывается
     *     {@link DataValidationException}.</li>
     *     <li>Пользователь должен существовать — иначе выбрасывается {@code EntityNotFoundException}.</li>
     * </ul>
     *
     * @param userId       идентификатор пользователя, добавляющего образование
     * @param educationDto объект {@link EducationViewDto}, содержащий данные для создания записи
     * @return объект {@link EducationViewDto}, представляющий созданную запись
     */
    EducationViewDto addEducation(long userId, EducationViewDto educationDto);

    /**
     * Обновляет существующую запись об образовании пользователя.
     * <p>
     * Условия:
     * <ul>
     *     <li>Дата начала обучения {@code from} не может быть в будущем — иначе выбрасывается
     *     {@link DataValidationException}.</li>
     *     <li>Обновление разрешено только владельцу записи — иначе выбрасывается
     *     {@link ForbiddenException}.</li>
     *     <li>Запись должна существовать — иначе выбрасывается {@code EntityNotFoundException}.</li>
     * </ul>
     *
     * @param userId       идентификатор пользователя, выполняющего обновление
     * @param educationId  идентификатор обновляемой записи
     * @param educationDto объект {@link EducationViewDto}, содержащий обновлённые данные
     * @return объект {@link EducationViewDto}, представляющий обновлённую запись
     */
    EducationViewDto updateEducation(long userId, long educationId, EducationViewDto educationDto);

    /**
     * Возвращает запись об образовании по её идентификатору.
     * <p>
     * Условия:
     * <ul>
     *     <li>Если запись не найдена — выбрасывается {@code EntityNotFoundException}.</li>
     * </ul>
     *
     * @param educationId идентификатор записи об образовании
     * @return объект {@link EducationViewDto}, представляющий найденную запись
     */
    EducationViewDto getById(long educationId);
}
