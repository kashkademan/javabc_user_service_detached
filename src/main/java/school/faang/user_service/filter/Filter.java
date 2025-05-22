package school.faang.user_service.filter;

public interface Filter<T, P> {

    boolean isApplicable(T filterDto);

    P apply(P filteredData, T filterDto);
}