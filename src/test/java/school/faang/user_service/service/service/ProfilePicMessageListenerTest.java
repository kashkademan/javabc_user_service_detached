package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.listener.ProfilePicMessageListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilePicMessageListenerTest {

    @InjectMocks
    private ProfilePicMessageListener profilePicMessageListener;

    @Spy
    @InjectMocks
    private ProfilePicMessageListener listenerSpy;

    @Test
    void testHandleProfilePicChangeShouldPrintMessage() {
        String testMessage = "User 1 updated profile picture";
        profilePicMessageListener.handleProfilePicChange(testMessage);
    }

    @Test
    void testProfilePicListenerAdapter_ShouldReturnConfiguredAdapter() {
        MessageListenerAdapter adapter = profilePicMessageListener.profilePicListenerAdapter(listenerSpy);

        assertNotNull(adapter, "Adapter should not be null");
        assertEquals(listenerSpy, adapter.getDelegate(), "Delegate should be the listener instance");

        verifyNoInteractions(listenerSpy);
    }
}