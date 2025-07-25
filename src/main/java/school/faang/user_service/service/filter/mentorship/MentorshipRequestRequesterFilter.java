package school.faang.user_service.service.filter.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MentorshipRequestRequesterFilter implements Filter<MentorshipRequest, MentorshipRequestFilterDto> {

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto dto) {
        return dto.getRequesterId() != null;
    }

    @Override
    public Stream<MentorshipRequest> filter(Stream<MentorshipRequest> entities, MentorshipRequestFilterDto dto) {
        Long requesterId = dto.getRequesterId();
        return entities.filter(entity -> entity.getRequester().getId().equals(requesterId));
    }
}