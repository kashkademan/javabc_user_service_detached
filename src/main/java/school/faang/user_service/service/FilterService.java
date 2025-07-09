package school.faang.user_service.service;

import java.util.List;

public interface FilterService<E, D> {
    List<E> toList(List<E> entities, D dto);
}
