package school.faang.user_service.remover;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.PremiumService;

@Component
@AllArgsConstructor
public class PremiumRemover {
    private final PremiumService premiumService;

    public ResponseEntity<String> removePremium() {
        String result = premiumService.removePremium();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    }

}
