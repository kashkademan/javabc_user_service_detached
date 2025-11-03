package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;

import java.util.stream.Stream;

public class MentorshipRequestStatusFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filter) {
        return filter.status() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> stream, MentorshipRequestFilterDto filter) {
        return isApplicable(filter)
                ? stream.filter(r -> r.getStatus().equals(filter.status())) : stream;
    }
}
