package school.faang.user_service.client;

import feign.RetryableException;
import feign.Retryer;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class DiceBearRetryer implements Retryer {
    private static final int MAX_ATTEMPTS = 3;
    private static final int BACKOFF = 1000;
    private int attempt = 0;

    public void continueOrPropagate(RetryableException e) {
        if (attempt++ >= MAX_ATTEMPTS) {
            log.error("Превышено максимальное количество попыток для запроса. Ошибка: {}",
                    e.getMessage());
            throw e;
        }
        try {
            Thread.sleep(BACKOFF);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw e;
        }
        log.info("Попытка повторить запрос...");
    }

    @Override
    public Retryer clone() {
        return new DiceBearRetryer();
    }
}