package school.faang.user_service.service.filter;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.event.Event;

/**
 * TitleContainsFilter — фильтр для проверки наличия ключевого слова в заголовке события.
 * <p>
 * Возвращает true, если заголовок события содержит указанное ключевое слово (без учёта регистра).
 * Если ключевое слово равно null, фильтр всегда пропускает событие.
 * </p>
 *
 * <p>Используется для поиска событий по ключевым словам в заголовке.</p>
 *
 * @author agent
 * @since 04.07.2025
 */
@RequiredArgsConstructor
public class TitleContainsFilter implements EventFilter {
    private final String keyword;

    @Override
    public boolean test(Event event) {
        return keyword == null || event.getTitle().toLowerCase().contains(keyword.toLowerCase());
    }
}