package school.faang.user_service.filter.mentorship_request;

import jakarta.validation.Valid;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Arrays;
import java.util.stream.Stream;

public class DescriptionFilter implements RequestFilter {
    @Override
    public boolean isApplicable(@Valid MentorshipRequestFilterDto filterDto) {
        return true;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, MentorshipRequestFilterDto filters) {
        return requests.filter(request -> Arrays.stream(filters.description().split(" "))
                .anyMatch(request1 -> request.getDescription().toLowerCase().contains(request1)));
    }
}
