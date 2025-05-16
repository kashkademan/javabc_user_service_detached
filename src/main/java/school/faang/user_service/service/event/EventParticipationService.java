package school.faang.user_service.service.event;

import school.faang.user_service.entity.User;

import java.util.List;

public interface EventParticipationService {

    void eventParticipant(long eventId);

    void unregisterParticipant(long eventId);

    List<User> getParticipant(long eventId);

    int getParticipantsCount(long eventId);
}
