package school.faang.user_service.scheduler;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.service.event.EventService;

import static org.mockito.Mockito.verify;

public class SchedulerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private Scheduler scheduler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testClearEvents() {
        scheduler.clearEvents();

        verify(eventService).clearPassedEvents();
    }
}