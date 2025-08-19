package school.faang.user_service.controller.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.analytics.ProfileVisitViewDto;
import school.faang.user_service.service.analytics.ProfileVisitService;

import java.util.List;

/**
 * ProfileVisitController — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
@RestController
@RequestMapping("/analytics/user/visits")
@RequiredArgsConstructor
public class ProfileVisitController {
    private final ProfileVisitService service;

    @GetMapping("/{visitedId}")
    public List<ProfileVisitViewDto> getProfileVisits(@PathVariable Long visitedId,
                                                      @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                      @RequestParam(value = "offset", defaultValue = "0") int offset) {
        return service.getUserVisitors(visitedId, limit, offset);
    }
}
