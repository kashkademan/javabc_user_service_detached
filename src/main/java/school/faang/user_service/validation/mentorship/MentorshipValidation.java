package school.faang.user_service.validation.mentorship;

import java.util.function.BiFunction;

public interface MentorshipValidation {
    boolean canAddMentorship(long mentorId, long menteeId, BiFunction<Long, Long, Boolean> biFunction);
}
