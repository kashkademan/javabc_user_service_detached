package school.faang.user_service.service.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

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
@Component
@RequiredArgsConstructor
public class DescriptionContainsFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto dto) {
        return dto.getDescriptionContains() != null;
    }

    @Override
    public Stream<Event> filter(Stream<Event> events, EventFilterDto dto) {
        String keyword = dto.getDescriptionContains().toLowerCase();
        return events.filter(event -> event.getDescription().contains(keyword));
    }
}