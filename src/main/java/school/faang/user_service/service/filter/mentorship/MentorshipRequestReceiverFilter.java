package school.faang.user_service.service.filter.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.service.filter.Filter;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MentorshipRequestReceiverFilter implements Filter<MentorshipRequest, MentorshipRequestFilterDto> {

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto dto) {
        return dto.receiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> filter(Stream<MentorshipRequest> entities, MentorshipRequestFilterDto dto) {
        Long receiverId = dto.receiverId();
        return entities.filter(entity -> entity.getReceiver().getId().equals(receiverId));
    }
}