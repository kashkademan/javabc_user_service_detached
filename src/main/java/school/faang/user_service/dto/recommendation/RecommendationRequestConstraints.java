package school.faang.user_service.dto.recommendation;

/**
 * Класс, содержащий константы и ограничения для работы с запросами рекомендаций.
 * <p>
 * Содержит максимальный допустимый размер сообщения и стандартные сообщения об ошибках валидации.
 * Все константы объявлены {@code public static final} и могут быть использованы в DTO классах
 * в аннотации валидации
 * </p>*
 *
 * @author mazin
 * @since 08.07.2025
 */
public class RecommendationRequestConstraints {
    public static final int MAX_SIZE_STRING = 512;
    public static final String MESSAGE_SIZE_INVALID = "Message size should be at most "
            + MAX_SIZE_STRING + " characters";
    public static final String EITHER_REQUESTER_OR_RECEIVER_REQUIRED = "Either requesterId "
            + "or receiverId must be provided";
}