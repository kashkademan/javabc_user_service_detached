package school.faang.user_service.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FilterService<E, D> {
    List<E> toList(List<E> entities, D dto);
}
