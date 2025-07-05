package school.faang.user_service.service;

import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface Filter<E, D> {
    boolean isApplicable(D filterDto);
    Stream<E> filter(Stream<E> entities, D dto);
}
