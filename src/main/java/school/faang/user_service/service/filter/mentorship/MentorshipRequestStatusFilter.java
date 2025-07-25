package school.faang.user_service.service.filter.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MentorshipRequestStatusFilter implements Filter<MentorshipRequest, MentorshipRequestFilterDto> {

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto dto) {
        return dto.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> filter(Stream<MentorshipRequest> entities, MentorshipRequestFilterDto dto) {
        RequestStatus targetStatus = dto.getStatus();
        return entities.filter(entity -> entity.getStatus().equals(targetStatus));
    }
}