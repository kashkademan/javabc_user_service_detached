package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.mentorship.MentorshipFilter;
import java.util.stream.Stream;

@Component
public class ReceiverIdFilter implements MentorshipFilter {
    @Override
    public boolean isApplicable(MentorshipFilterDto filterDto) {
        return filterDto.receiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests, MentorshipFilterDto filterDto) {
        return mentorshipRequests
                .filter(request -> filterDto.receiverId().equals(request.getReceiver()));
    }
}
