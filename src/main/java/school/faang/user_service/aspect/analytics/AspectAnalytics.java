package school.faang.user_service.aspect.analytics;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.analytics.AnalyticsService;

@RequiredArgsConstructor
@Component
@Aspect
public class AspectAnalytics {
    private final AnalyticsService analyticsService;

    @AfterReturning("@annotation(AnalyticsProfileView) && args(userId,..)")
    public void publishProfileViewEvent(long userId) {
        analyticsService.publishProfileViewEvent(userId);
    }
}
