package school.faang.user_service.service.event;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.EventCleanupException;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Сервис для работы с событиями
 *
 * @author Linempy
 * @see EventService
 * @since 06.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    @Value("${event.cleanup.batch-size}")
    private int batchSize;
    @Value("${event.cleanup.timeout-sec}")
    private long cleanupTimeoutSeconds;
    private final EventRepository repository;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void cleanupExpiredEvents() {
        try {
            List<Long> passedEvents = repository.findExpiredEventIds();

            if (passedEvents.isEmpty()) {
                log.info("Список ивентов пуст");
                return;
            }

            AtomicInteger counter = new AtomicInteger(0);
            int totalDeleted = processAndCountDeletion(counter, passedEvents);
            log.info("Успешно удалено {} просроченных ивентов", totalDeleted);

        } catch (DataAccessException e) {
            log.error("Ошибка базы данных во время удаления ивентов", e);
            throw new EventCleanupException("Ошибка базы данных", e);
        } catch (Exception e) {
            log.error("Неожиданная ошибка во время удаления ивентов", e);
            throw new EventCleanupException("Ошибка во время удаления ивентов", e);
        }
    }

    private int processAndCountDeletion(AtomicInteger counter, List<Long> ids) {
        List<CompletableFuture<Void>> futures = createBatchDeletionFutures(counter, ids);
        waitForCompletion(futures);
        return counter.get();
    }

    private void waitForCompletion(List<CompletableFuture<Void>> futures) {
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(cleanupTimeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("Таймаут при удалении ивентов");
            throw new EventCleanupException("Таймаут операции");
        } catch (ExecutionException e) {
            log.error("Удаление ивентов завершилось ошибкой");
            throw new EventCleanupException("Выполнение завершилось неудачей", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Удаление ивентов было прервано");
            throw new EventCleanupException("Операция прервана", e);
        }
    }


    private List<CompletableFuture<Void>> createBatchDeletionFutures(AtomicInteger counter, List<Long> passedEvents) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (List<Long> ids : Lists.partition(passedEvents, batchSize)) {
            try {
                CompletableFuture<Void> future = CompletableFuture.runAsync(
                        () -> processBatchDeletion(ids, counter),
                        taskExecutor
                ).exceptionally(e -> {
                    log.error("Один из пакетов завершился с ошибкой", e);
                    return null;
                });

                futures.add(future);
            } catch (TaskRejectedException e) {
                log.warn("Задача отменена, происходит выполнение в текущем потоке");
                processBatchDeletion(ids, counter);
            }
        }

        return futures;
    }

    private void processBatchDeletion(List<Long> ids, AtomicInteger counter) {
        try {
            int deletedCount = repository.deleteByIds(ids);
            counter.addAndGet(deletedCount);
            log.debug("Удалено {}/{} ивентов из списка", deletedCount, ids.size());
        } catch (DataAccessException e) {
            log.error("Не удалось выполнить пакетное удаление", e);
            throw new EventCleanupException("Не удалось выполнить удаление списка ивентов", e);
        }
    }
}