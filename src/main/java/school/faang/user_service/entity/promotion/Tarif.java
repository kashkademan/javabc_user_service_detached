package school.faang.user_service.entity.promotion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Tarif {
    BASIC(10000),
    ADVANCED(1000),
    EXPERIENCE(100),
    LEGEND(10);

    private final Integer scopeForTarif;
}
