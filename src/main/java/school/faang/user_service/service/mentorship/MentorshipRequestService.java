package school.faang.user_service.service.mentorship;


import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;

import java.util.List;

public interface MentorshipRequestService {

    /**
     * Отправить запрос на менторство.
     */
    MentorshipRequestDto requestMentorship(MentorshipRequestDto dto);

    /**
     * Получить все запросы на менторство с фильтрами.
     */
    List<MentorshipRequestDto> getRequests(RequestFilterDto filter);

    /**
     * Принять запрос на менторство.
     */
    void acceptRequest(long id);

    /**
     * Отклонить запрос на менторство.
     */
    void rejectRequest(long id, RejectionDto rejection);
}
