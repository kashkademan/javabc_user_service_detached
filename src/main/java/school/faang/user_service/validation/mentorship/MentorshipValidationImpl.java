package school.faang.user_service.validation.mentorship;

import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class MentorshipValidationImpl implements MentorshipValidation {

    @Override
    public boolean canAddMentorship(long mentorId, long menteeId, BiFunction<Long, Long, Boolean> biFunction) {
        return biFunction.apply(mentorId, menteeId);
    }
}
