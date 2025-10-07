package school.faang.user_service.repository.mentorship.service.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

@Service
@Slf4j
public class MentorshipServiceValidation {
    public boolean validationCurrentUser(long currentUserId, long mentorId, long menteeId) {
        if (currentUserId != menteeId && currentUserId != mentorId) {
            log.warn("You can only manage your own mentorship relations");
            throw new ForbiddenException("You can only manage your own mentorship relations");
        } else {
            return true;
        }
    }

    public boolean validationIds(long mentorId, long menteeId) {
        if (menteeId == mentorId) {
            log.warn("User cannot be mentor for themselves");
            throw new DataValidationException("User cannot be mentor for themselves");
        } else {
            return true;
        }
    }
}
