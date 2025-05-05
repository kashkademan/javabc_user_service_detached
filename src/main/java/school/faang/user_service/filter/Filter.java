package school.faang.user_service.filter;

import java.util.stream.Stream;

public interface Filter<T, R> {
    boolean isApplicable(T t);

    Stream<R> apply(Stream<R> r, T t);
}
