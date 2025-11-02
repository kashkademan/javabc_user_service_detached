package school.faang.user_service.filter;

import java.util.stream.Stream;

public interface Filter<T, V> {

    boolean isApplicable(V eventFilterDto);

    Stream<T> apply(Stream<T> events, V eventFilterDto);
}
