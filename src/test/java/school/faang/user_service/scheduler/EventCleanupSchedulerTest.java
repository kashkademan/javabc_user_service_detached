package school.faang.user_service.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.event.EventService;

import static org.mockito.Mockito.verify;

/**
 * Тестовый класс для {@link EventCleanupScheduler}.
 * <p>
 * Проверяет корректность работы планировщика очистки событий.
 * Основная задача - убедиться, что планировщик действительно вызывает
 * соответствующий сервисный метод в нужное время.
 * </p>
 *
 * @author Linempy
 * @since 07.08.2025
 */
@DisplayName("Тестирование планировщика очистки событий")
@ExtendWith(MockitoExtension.class)
class EventCleanupSchedulerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventCleanupScheduler scheduler;

    @Test
    @DisplayName("Проверка на успешный вызов сервисного метода")
    void testScheduleCleanupShouldInvokeMethod() {
        scheduler.scheduleEventCleanup();
        verify(eventService).cleanupExpiredEvents();
    }
}