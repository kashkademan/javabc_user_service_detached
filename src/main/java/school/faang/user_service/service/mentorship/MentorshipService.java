package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.user.UserDto;

import java.util.List;

/**
 * Service interface for managing mentorship relationships between users.
 * This interface provides methods to create, retrieve, and delete relationships.
 */
public interface MentorshipService {
    /**
     * Creates a mentorship relationship between a mentor and a mentee.
     *
     * @param mentorId the identifier of the mentor
     * @param menteeId the identifier of the mentee
     * @throws IllegalArgumentException                                    if {@code mentorId} equals {@code menteeId}
     * @throws school.faang.user_service.exception.DataValidationException if validation fails
     */
    void addMentorship(long mentorId, long menteeId);

    /**
     * Retrieves all mentees for a given mentor.
     *
     * @param userId the identifier of the mentor
     * @return a list of {@link UserDto} representing all mentees of the given mentor;
     * an empty list if the mentor has no mentees
     */
    List<UserDto> getMentees(long userId);

    /**
     * Retrieves all mentors for a given mentee.
     *
     * @param userId the identifier of the mentee
     * @return a list of {@link UserDto} representing all mentors of the given mentee;
     * an empty list if the mentee has no mentors
     */
    List<UserDto> getMentors(long userId);

    /**
     * Deletes the mentorship between a given mentor and mentee.
     * Only a participant of the mentorship (the mentor or the mentee) can delete the mentorship.
     *
     * @param menteeId the identifier of the mentee
     * @param mentorId the identifier of the mentor
     * @throws school.faang.user_service.exception.ForbiddenException if the current user is not a participant in the mentorship
     */
    void deleteMentorship(long menteeId, long mentorId);
}
