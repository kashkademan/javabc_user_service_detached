package school.faang.user_service.remover;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.PremiumService;

import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class PremiumRemover {
    private final PremiumService premiumService;


    @Scheduled(cron = "${app.scheduler.premium-remove.cron}")
    public ResponseEntity<CompletableFuture<String>> removePremium() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(premiumService.removePremium());
    }

}
