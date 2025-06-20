package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.service.premium.PremiumService;

@RestController
@RequestMapping("/api/v1/premium")
@RequiredArgsConstructor
public class PremiumController {
    private final PremiumService premiumService;
    private final PremiumMapper premiumMapper;

    @PostMapping("/days/{daysAmount}")
    public ResponseEntity<PremiumDto> buyPremium(@PathVariable int daysAmount) {
        Premium premium = premiumService.buyPremium(daysAmount);
        PremiumDto premiumDto = premiumMapper.toPremiumDto(premium);
        return ResponseEntity.ok(premiumDto);
    }

}
