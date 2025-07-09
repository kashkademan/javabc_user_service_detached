package school.faang.user_service.filter.MentorshipRequestFilter;

import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(RequestFilterDto requestFilterDto);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequestStream, RequestFilterDto requestFilterDto);
}
