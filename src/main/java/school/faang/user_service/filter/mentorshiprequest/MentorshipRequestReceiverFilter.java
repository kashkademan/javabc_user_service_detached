package school.faang.user_service.filter.mentorshiprequest;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public class MentorshipRequestReceiverFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getReceiverIdPattern() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto requestFilterDto) {
        return requests.filter(request -> request.getReceiver().getId().equals(requestFilterDto.getReceiverIdPattern()));
    }
}