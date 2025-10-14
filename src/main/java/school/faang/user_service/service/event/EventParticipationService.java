package school.faang.user_service.service.event;

import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;

/*
 * Service interface for managing events participants
 * This interface provides methods to register, unregister, get count and get list of participants on the event
 */
public interface EventParticipationService {
    /**
     * Register a user for an event
     *
     * @param eventId the identifier of the event
     * @param userId the identifier of the user
     * @throws school.faang.user_service.exception.DataValidationException if user has already registered for an event
     */
    void registerParticipant(long eventId, long userId);

    /**
     * Unregister a user from an event
     * Only a user registered for an event can unregister from the event
     *
     * @param eventId the identifier of the event
     * @param userId the identifier of the user
     * @throws school.faang.user_service.exception.DataValidationException if user hasn't registered for an event
     * @throws school.faang.user_service.exception.ForbiddenException if user try to remove someone else from the event
     */
    void unregisterParticipant(long eventId, long userId);

    /**
     * Get count of participants for an event
     *
     * @param eventId the identifier of the event
     * @return a count {@link CountResponse} of all participants for the given event;
     * a zero if the event has no any participants
     */
    CountResponse countParticipantsByEventId(long eventId);

    /**
     * Get count of participants for an event
     *
     * @param eventId the identifier of the event
     * @return a list of {@link UserDto} representing all participants for the given event;
     * an empty list if the event has no any participants
     */
    List<UserDto> getAllParticipantsByEventId(long eventId);
}
