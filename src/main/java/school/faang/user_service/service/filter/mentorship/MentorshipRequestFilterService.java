package school.faang.user_service.service.filter.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.service.filter.Filter;
import school.faang.user_service.service.filter.FilterService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipRequestFilterService implements FilterService<MentorshipRequest, MentorshipRequestFilterDto> {

    private final List<Filter<MentorshipRequest, MentorshipRequestFilterDto>> filters;

    @Override
    public List<MentorshipRequest> getFilteredList(List<MentorshipRequest> entities, MentorshipRequestFilterDto dto) {
        return applyFilters(filters, entities, dto);
    }
}