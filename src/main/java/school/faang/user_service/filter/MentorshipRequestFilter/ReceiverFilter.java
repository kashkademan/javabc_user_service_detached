package school.faang.user_service.filter.MentorshipRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class ReceiverFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getReceiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequestStream, RequestFilterDto requestFilterDto) {
        return mentorshipRequestStream
                .filter(request -> request.getReceiver().getId().equals(requestFilterDto.getReceiverId()));
    }
}
