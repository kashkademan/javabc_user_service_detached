package school.faang.user_service.service.analytics;

import school.faang.user_service.dto.analytics.ProfileVisitCreateDto;
import school.faang.user_service.dto.analytics.ProfileVisitViewDto;

import java.util.List;

/**
 * ProfileVisitService — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
public interface ProfileVisitService {
    void addVisit(ProfileVisitCreateDto visit);

    List<ProfileVisitViewDto> getUserVisitors(Long visitedId, int limit, int offset);
}
