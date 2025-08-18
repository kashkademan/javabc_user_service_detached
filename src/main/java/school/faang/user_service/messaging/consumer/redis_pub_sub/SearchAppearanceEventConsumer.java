package school.faang.user_service.messaging.consumer.redis_pub_sub;

import org.springframework.context.event.EventListener;
import school.faang.user_service.messaging.consumer.EventConsumer;
import school.faang.user_service.messaging.dto.SearchAppearanceEvent;

/**
 * SearchAppearanceEventListener — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
public class SearchAppearanceEventConsumer implements EventConsumer<SearchAppearanceEvent> {
    @Override
    @EventListener()
    public void listen(SearchAppearanceEvent event) {

    }
}
