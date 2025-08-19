package school.faang.user_service.service.event;

import school.faang.user_service.exception.EventCleanupException;

/**
 * Сервис для управления событиями пользователей.
 * <p>
 * Предоставляет методы для работы с событиями, включая очистку просроченных событий.
 * Все операции выполняются асинхронно с использованием настроенного пула потоков.
 * </p>
 *
 * @author Linempy
 * @since 06.08.2025
 */
public interface EventService {

    /**
     * Очищает просроченные события пользователей.
     * <p>
     * Метод находит все события, срок которых истек, и удаляет их из системы.
     * Удаление выполняется пакетами (batch) для оптимизации производительности.
     * </p>
     *
     * @throws EventCleanupException если произошла ошибка при удалении событий
     */
    void cleanupExpiredEvents();
}