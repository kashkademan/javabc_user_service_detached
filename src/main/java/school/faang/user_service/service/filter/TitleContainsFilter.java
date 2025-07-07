package school.faang.user_service.service.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

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
@Component
@RequiredArgsConstructor
public class TitleContainsFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto dto) {
        return dto.getTitleContains() != null;
    }

    @Override
    public Stream<Event> filter(Stream<Event> events, EventFilterDto dto) {
        String keyword = dto.getTitleContains().toLowerCase();
        return events.filter(event -> event.getTitle().toLowerCase().contains(keyword));
    }
}