package school.faang.user_service.filter.mentorship;

import org.jetbrains.annotations.NotNull;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(@NotNull MentorshipRequestFilterDto mentorshipRequestFilterDto);

    Stream<MentorshipRequest> apply(
            @NotNull Stream<MentorshipRequest> mentorshipRequest,
            MentorshipRequestFilterDto mentorshipRequestFilterDto);
}