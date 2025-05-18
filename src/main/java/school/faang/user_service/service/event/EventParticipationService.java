package school.faang.user_service.service.event;

import school.faang.user_service.entity.User;

import java.util.List;

public interface EventParticipationService {
    User registerParticipant(long eventId);

    void unregisterParticipant(long eventId);

    List<User> getParticipant(long eventId);

    int getParticipantsCount(long eventId);
}
