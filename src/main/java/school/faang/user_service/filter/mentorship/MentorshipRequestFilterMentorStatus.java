package school.faang.user_service.filter.mentorship;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class MentorshipRequestFilterMentorStatus implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(@NotNull MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.status() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(
            @NotNull Stream<MentorshipRequest> mentorshipRequest,
            MentorshipRequestFilterDto mentorshipRequestFilterDto) {

        return mentorshipRequest.filter((mentorshipR) -> Objects.equals(
                mentorshipRequestFilterDto.status(),
                mentorshipR.getStatus()));
    }
}