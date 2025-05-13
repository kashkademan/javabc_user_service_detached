package school.faang.user_service.filters.mentorship_request;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Arrays;
import java.util.stream.Stream;

public class DescriptionFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getDescription() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        return requests.filter(request -> Arrays.stream(filters.getDescription().split(" "))
                .anyMatch(request1 -> request1.contains(filters.getDescription())));
    }
}
