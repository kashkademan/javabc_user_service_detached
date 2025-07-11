package school.faang.user_service.controller.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.UserWithPremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumServiceImpl;

import java.util.List;

/**
 * REST контроллер для управления функционалом премиум-подписки пользователей.
 * <p>
 * Предоставляет конечные точки для покупки премиума и получения списка пользователей с активной премиум-подпиской.
 * </p>
 *
 * <ul>
 *     <li>POST /premium/buy — покупка премиума на указанный период в днях.</li>
 *     <li>GET /premium/active-users — получение списка пользователей с активной премиум-подпиской.</li>
 * </ul>
 *
 * <p>
 * Использует {@link PremiumServiceImpl} для бизнес-логики и
 * {@link UserContext} для извлечения данных текущего пользователя.
 * </p>
 *
 * @author agent
 * @since 10.07.2025
 */
@RestController
@RequestMapping("/premium")
@RequiredArgsConstructor
public class PremiumController {

    private final PremiumServiceImpl service;
    private final UserContext context;

    @PostMapping("/buy")
    public ResponseEntity<PremiumDto> buyPremium(@RequestParam int days) {
        PremiumPeriod period = PremiumPeriod.getPremiumPeriod(days);
        PremiumDto premiumDto = service.buyPremium(context.getUserId(), period);
        return ResponseEntity.ok(premiumDto);
    }

    @GetMapping("/active-users")
    public ResponseEntity<List<UserWithPremiumDto>> getUsersWithActivePremium() {
        List<UserWithPremiumDto> usersWithPremium = service.getUsersWithActivePremium();
        return ResponseEntity.ok(usersWithPremium);
    }
}