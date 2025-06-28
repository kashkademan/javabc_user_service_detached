package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import school.faang.user_service.event.ProfilePicEvent;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ProfilePicEventTest {

    @Test
    void testNoArgsConstructor() {
        ProfilePicEvent event = new ProfilePicEvent();

        assertNull(event.getUserId());
        assertNull(event.getNewFileId());
        assertNull(event.getNewSmallFileId());
        assertNull(event.getOldFileId());
        assertNull(event.getOldSmallFileId());
        assertNull(event.getChangedAt());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ProfilePicEvent event = new ProfilePicEvent(
                1L, "new.jpg", "new_small.jpg", "old.jpg", "old_small.jpg", now);

        assertEquals(1L, event.getUserId());
        assertEquals("new.jpg", event.getNewFileId());
        assertEquals("new_small.jpg", event.getNewSmallFileId());
        assertEquals("old.jpg", event.getOldFileId());
        assertEquals("old_small.jpg", event.getOldSmallFileId());
        assertEquals(now, event.getChangedAt());
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        ProfilePicEvent event = ProfilePicEvent.builder()
                .userId(1L)
                .newFileId("new.jpg")
                .newSmallFileId("new_small.jpg")
                .oldFileId("old.jpg")
                .oldSmallFileId("old_small.jpg")
                .changedAt(now)
                .build();

        assertEquals(1L, event.getUserId());
        assertEquals("new.jpg", event.getNewFileId());
        assertEquals("new_small.jpg", event.getNewSmallFileId());
        assertEquals("old.jpg", event.getOldFileId());
        assertEquals("old_small.jpg", event.getOldSmallFileId());
        assertEquals(now, event.getChangedAt());
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        ProfilePicEvent event = new ProfilePicEvent();

        event.setUserId(1L);
        event.setNewFileId("new.jpg");
        event.setNewSmallFileId("new_small.jpg");
        event.setOldFileId("old.jpg");
        event.setOldSmallFileId("old_small.jpg");
        event.setChangedAt(now);

        assertEquals(1L, event.getUserId());
        assertEquals("new.jpg", event.getNewFileId());
        assertEquals("new_small.jpg", event.getNewSmallFileId());
        assertEquals("old.jpg", event.getOldFileId());
        assertEquals("old_small.jpg", event.getOldSmallFileId());
        assertEquals(now, event.getChangedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        ProfilePicEvent event1 = ProfilePicEvent.builder()
                .userId(1L)
                .newFileId("new.jpg")
                .changedAt(now)
                .build();

        ProfilePicEvent event2 = ProfilePicEvent.builder()
                .userId(1L)
                .newFileId("new.jpg")
                .changedAt(now)
                .build();

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void testToString() {
        ProfilePicEvent event = ProfilePicEvent.builder()
                .userId(1L)
                .newFileId("new.jpg")
                .build();

        String toStringResult = event.toString();
        assertTrue(toStringResult.contains("ProfilePicEvent"));
        assertTrue(toStringResult.contains("userId=1"));
        assertTrue(toStringResult.contains("newFileId=new.jpg"));
    }
}
