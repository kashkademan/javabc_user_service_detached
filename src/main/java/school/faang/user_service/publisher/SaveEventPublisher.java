package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Универсальный компонент для безопасной публикации событий после коммита транзакции
 * с поддержкой повторных попыток
 *
 * @param <E> тип события
 * @author Linempy
 * @since 27.08.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaveEventPublisher<E> {

    private final EventPublisher<E> publisher;
    private final RetryTemplate retryTemplate;

    public void publishAfterCommit(E event) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            retryTemplate.execute(context -> {
                                publisher.publish(event);
                                return null;
                            });
                        } catch (Exception e) {
                            log.error("Ошибка отправки ивента после повторной отправки: {}", event, e);
                        }
                    }
                }
        );
    }
}