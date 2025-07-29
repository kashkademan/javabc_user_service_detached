package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Тестирует функциональность фильтра {@link RecommendationRequestMessageFilter},
 * который отбирает запросы по вхождению строки {@code message}
 * <p>
 * Проверяем следующие сценарии:
 * <ul>
 *     <li>Фильтра применяется только когда строка {@code message} не null
 *     и не пустая (в том числе пробелы)</li>
 *     <li>Корректность фильтрации запросов по {@code message}</li>
 *     <li>Регистр независимость</li>
 *     <li>Отсутствие совпадений</li>
 * </ul>
 * </p>
 *
 * @author Linempy
 * @since 13.07.2025
 */
public class RecommendationRequestMessageFilterTest {

    private final RecommendationRequestMessageFilter filter = new RecommendationRequestMessageFilter();

    @Test
    @DisplayName("Проверка на успешное использование фильтра сообщений")
    public void testIsApplicableTrue() {
        boolean result = preparingData("No blank");
        assertTrue(result);
    }

    @Test
    @DisplayName("Проверка, что фильтр имеет пустое сообщение")
    public void testIsApplicableFalseWhenMessageIsEmpty() {
        boolean result = preparingData("");
        assertFalse(result);
    }

    @Test
    @DisplayName("Проверка, что фильтр имеет сообщение из пробелов")
    public void testIsApplicableFalseWhenMessageIsBlank() {
        boolean result = preparingData("   ");
        assertFalse(result);
    }

    @Test
    @DisplayName("Проверка, что фильтр имеет null сообщение")
    public void testIsApplicableFalseWhenMessageIsNull() {
        boolean result = preparingData(null);
        assertFalse(result);
    }

    @Test
    @DisplayName("Проверка на корректность фильтрации")
    public void testApplySuccessful() {
        RecommendationRequestFilterDto filterDto = getFilterDto("test");
        Stream<RecommendationRequest> requests = Stream.of(
                RecommendationRequest.builder().message("-- test --").build(),
                RecommendationRequest.builder().message("").build(),
                RecommendationRequest.builder().message("   ").build(),
                RecommendationRequest.builder().message("test message").build()
        );

        List<RecommendationRequest> results = filter.apply(requests, filterDto).toList();

        assertEquals(2, results.size());
        assertTrue(results.get(0).getMessage().contains(filterDto.messageContains()));

    }

    @Test
    @DisplayName("Проверка на корректность регистр независимости фильтрации")
    public void testApplySuccessfulWhenDifferentRegister() {
        RecommendationRequestFilterDto filterDto = getFilterDto("tEst");
        Stream<RecommendationRequest> requests = Stream.of(
                RecommendationRequest.builder().message("-- TeSt --").build(),
                RecommendationRequest.builder().message("").build(),
                RecommendationRequest.builder().message("   ").build(),
                RecommendationRequest.builder().message("Some message").build()
        );

        List<RecommendationRequest> results = filter.apply(requests, filterDto).toList();
        boolean result = results.get(0).getMessage().toLowerCase().contains(filterDto.messageContains().toLowerCase());

        assertEquals(1, results.size());
        assertTrue(result);
    }

    @Test
    @DisplayName("Проверка, что в сообщении не содержится переданная строка")
    public void testApplySuccessfulWhenNotMatch() {
        RecommendationRequestFilterDto filterDto = getFilterDto("tEst");
        Stream<RecommendationRequest> requests = Stream.of(
                RecommendationRequest.builder().message("-- te st --").build(),
                RecommendationRequest.builder().message("").build(),
                RecommendationRequest.builder().message("   ").build(),
                RecommendationRequest.builder().message("Some message").build()
        );

        List<RecommendationRequest> results = filter.apply(requests, filterDto).toList();
        boolean result = results.isEmpty();

        assertEquals(0, results.size());
        assertTrue(result);
    }

    private boolean preparingData(String message) {
        RecommendationRequestFilterDto filterDto = getFilterDto(message);
        return filter.isApplicable(filterDto);
    }

    private RecommendationRequestFilterDto getFilterDto(String testMessage) {
        return new RecommendationRequestFilterDto(
                1L,
                2L,
                testMessage,
                RequestStatus.PENDING
        );
    }
}