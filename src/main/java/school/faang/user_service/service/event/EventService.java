package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;

import java.util.List;

/**
 * Сервис для управления событиями.
 * Предоставляет методы для создания, обновления, получения и удаления событий.
 */
public interface EventService {

    /**
     * Создаёт новое событие на основе переданных данных.
     * <p>
     * Условия:
     * <ul>
     *     <li>Пользователь, инициирующий создание, становится владельцем события.</li>
     *     <li>Если у владельца нет всех скиллов, указанных в relatedSkills,
     *     выбрасывается {@code DataValidationException}.</li>
     *     <li>Если пользователь не найден — выбрасывается {@code IllegalStateException}.</li>
     * </ul>
     *
     * @param eventDto объект {@link CreateEventDto}, содержащий данные для создания события
     * @return объект {@link EventDto}, представляющий созданное событие
     */
    EventDto create(CreateEventDto eventDto);

    /**
     * Обновляет существующее событие по его идентификатору.
     * <p>
     * Условия:
     * <ul>
     *     <li>Обновление разрешено только владельцу события — иначе выбрасывается
     *     {@code ForbiddenException}.</li>
     *     <li>Если событие не найдено — выбрасывается {@code EntityNotFoundException} или
     *     {@code NotFoundException}.</li>
     *     <li>Если указанные скиллы не соответствуют навыкам владельца — выбрасывается
     *     {@code DataValidationException}.</li>
     * </ul>
     *
     * @param eventId идентификатор обновляемого события
     * @param updateEventDto объект {@link UpdateEventDto}, содержащий обновлённые данные
     * @return объект {@link EventDto}, представляющий обновлённое событие
     */
    EventDto update(long eventId, UpdateEventDto updateEventDto);

    /**
     * Возвращает список событий, удовлетворяющих указанным фильтрам.
     * <p>
     * Поддерживаются фильтры:
     * <ul>
     *     <li>по названию (titleContains)</li>
     *     <li>по описанию (descriptionContains)</li>
     *     <li>по типу события (type)</li>
     *     <li>по ID владельца (ownerId)</li>
     *     <li>по ID участника (participantId)</li>
     * </ul>
     *
     * @param filters объект {@link EventFilterDto}, содержащий критерии фильтрации
     * @return список {@link EventDto}, соответствующих фильтру
     */
    List<EventDto> getByFilters(EventFilterDto filters);

    /**
     * Удаляет событие по его идентификатору.
     * <p>
     * Условия:
     * <ul>
     *     <li>Удаление разрешено только владельцу события — иначе выбрасывается
     *     {@code ForbiddenException}.</li>
     *     <li>Если событие не найдено — выбрасывается
     *     {@code EntityNotFoundException} или {@code NotFoundException}.</li>
     * </ul>
     *
     * @param eventId идентификатор удаляемого события
     */
    void delete(long eventId);
}
