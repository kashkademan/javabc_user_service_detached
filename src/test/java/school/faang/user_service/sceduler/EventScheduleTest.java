package school.faang.user_service.sceduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import school.faang.user_service.scheduler.EventScheduler;
import school.faang.user_service.service.event.EventService;

import static org.mockito.Mockito.*;

class EventScheduleTest {

    private EventScheduler eventScheduler;
    private EventService eventService;
    private Environment environment;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        environment = mock(Environment.class);
        eventScheduler = new EventScheduler(eventService, environment);
    }

    @Test
    void testInit_logsFrequency() {
        when(environment.getProperty("event.removal.cron")).thenReturn("0 0 12 * * ?");

        eventScheduler.init();

        verify(environment).getProperty("event.removal.cron");
    }

    @Test
    void testClearEvents_callsServiceAndLogsResult() {
        when(eventService.clearEvents()).thenReturn(5);

        eventScheduler.clearEvents();

        verify(eventService).clearEvents();
    }
}
