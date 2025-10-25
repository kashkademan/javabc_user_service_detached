package school.faang.user_service.service.mentorship.fiters;

import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;

import java.util.stream.Stream;

public class ForTestMentorshipReceiverFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filter) {
        return true;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> stream, MentorshipRequestFilterDto filter) {
        return stream.filter(r -> r.getReceiver().getId().equals(2L));
    }
}
