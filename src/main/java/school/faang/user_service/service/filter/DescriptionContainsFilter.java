package school.faang.user_service.service.filter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.event.Event;

/**
 * DescriptionContainsFilter — фильтр для проверки наличия ключевого слова в описании события.
 * <p>
 * Возвращает true, если описание события содержит указанное ключевое слово (игнорируя регистр).
 * Если ключевое слово равно null, фильтр всегда пропускает событие.
 * </p>
 *
 * @author agent
 * @since 04.07.2025
 */
@RequiredArgsConstructor
public class DescriptionContainsFilter implements EventFilter {
    private final String keyword;

    @Override
    public boolean test(Event event) {
        return keyword == null
               || event.getDescription().toLowerCase().contains(keyword.toLowerCase());
    }
}