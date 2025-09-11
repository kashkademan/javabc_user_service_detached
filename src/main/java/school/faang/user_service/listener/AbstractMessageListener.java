package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

/**
 * Общая логика слушателя ивентов
 *
 * @author Linempy
 * @since 23.08.2025
 */
@RequiredArgsConstructor
public abstract class AbstractMessageListener {

    protected final ObjectMapper objectMapper;
}