package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/premium")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;

    @PostMapping
    public PremiumDto buyPremium(@PathVariable long userId, @RequestParam int days) {
        PremiumPeriod period = PremiumPeriod.fromDays(days);
        log.info("Buying premium by user with id {} for {} days - Started", userId, period.getDays());
        PremiumDto premiumDto = premiumService.buyPremium(userId, period);
        log.info("Buying premium by user with id {} for {} days - Finished", userId, period.getDays());
        return premiumDto;
    }
}