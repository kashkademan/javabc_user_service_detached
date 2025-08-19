package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

/**
 * Планировщик для регулярной очистки устаревших событий из системы.
 * <p>
 * Класс выполняет автоматическую очистку событий, у которых истек срок окончания (endDate),
 * согласно расписанию, заданному в конфигурации. Использует Spring Scheduling для запуска задачи.
 * </p>
 *
 * @author Linempy
 * @since 06.08.2025
 */
@Component
@RequiredArgsConstructor
public class EventCleanupScheduler {

    private final EventService service;

    @Scheduled(cron = "${schedule.event-cleanup}", zone = "${schedule.timezone}")
    public void scheduleEventCleanup() {
        service.cleanupExpiredEvents();
    }
}