package school.faang.user_service.filter.mentorshiprequest;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(RequestFilterDto requestFilterDto);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto requestFilterDto);
}