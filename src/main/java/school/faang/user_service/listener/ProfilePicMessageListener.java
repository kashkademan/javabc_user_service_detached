package school.faang.user_service.listener;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class ProfilePicMessageListener {

    public void handleProfilePicChange(String message) {
        System.out.println("Profile picture changed: " + message);
    }

    @Bean
    public MessageListenerAdapter profilePicListenerAdapter(ProfilePicMessageListener listener) {
        return new MessageListenerAdapter(listener, "handleProfilePicChange");
    }
}