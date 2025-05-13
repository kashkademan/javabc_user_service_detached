package school.faang.user_service.repository.event;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;

@UtilityClass
public class EventSpecification {
    
    public static Specification<Event> withFilter(EventFilterDto filter) {
        Specification<Event> spec = Specification.where(null);
        
        if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
            spec = spec.and(hasTitle(filter.getTitle()));
        }
        
        if (filter.getStartDate() != null) {
            spec = spec.and(hasStartDateAfter(filter.getStartDate()));
        }
        
        if (filter.getOwnerId() != null) {
            spec = spec.and(hasOwner(filter.getOwnerId()));
        }
        
        return spec;
    }
    
    public static Specification<Event> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")),
                "%" + title.toLowerCase() + "%"
            );
    }
    
    public static Specification<Event> hasStartDateAfter(LocalDateTime startDate) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.greaterThanOrEqualTo(
                root.get("startDate"), startDate
            );
    }
    
    public static Specification<Event> hasOwner(Long ownerId) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(
                root.get("owner").get("id"), ownerId
            );
    }
}
