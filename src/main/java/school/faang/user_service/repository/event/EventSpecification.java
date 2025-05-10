package school.faang.user_service.repository.event;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;

public class EventSpecification {
    public static Specification<Event> withFilter(EventFilterDto filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + filter.getTitle().toLowerCase() + "%"
                ));
            }

            if (filter.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("startDate"), filter.getStartDate()
                ));
            }

            if (filter.getOwnerId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("owner").get("id"), filter.getOwnerId()
                ));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
