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
 * @see EventService
 *
 * @author Linempy
 * @since 06.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    @Value("${async.queue-capacity}")
    private int batchSize;
    @Value("${async.cleanup-timeout-seconds}")
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
            handleDatabaseError(e);
        } catch (Exception e) {
            handleUnexpectedError(e);
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
            throw new EventCleanupException("Выполнение завершилось неудачей", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EventCleanupException("Операция прервана", e);
        }
    }


    private List<CompletableFuture<Void>> createBatchDeletionFutures(AtomicInteger counter, List<Long> passedEvents) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (List<Long> batch : Lists.partition(passedEvents, batchSize)) {
            try {
                futures.add(CompletableFuture.runAsync(
                        () -> processBatchDeletion(batch, counter),
                        taskExecutor
                ));
            } catch (TaskRejectedException e) {
                processBatchDeletion(batch, counter);
            }
        }

        return futures;
    }

    private void processBatchDeletion(List<Long> batch, AtomicInteger counter) {
        try {
            int deletedCount = repository.deleteByIds(batch);
            counter.addAndGet(deletedCount);
            log.debug("Удалено {} events in batch", deletedCount);
        } catch (DataAccessException e) {
            log.error("Не удалось выполнить пакетное удаление", e);
            throw new EventCleanupException("Не удалось выполнить пакетное удаление", e);
        }
    }

    private void handleDatabaseError(DataAccessException e) {
        log.error("Ошибка базы данных во время удаления ивентов", e);
        throw e;
    }

    private void handleUnexpectedError(Exception e) {
        log.error("Неожиданная ошибка во время удаления ивентов", e);
        throw new EventCleanupException("Ошибка во время удаления ивентов", e);
    }

}