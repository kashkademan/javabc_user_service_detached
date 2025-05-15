package school.faang.user_service.repository.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.model.event.EventFilter;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EventFilterRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Event> findByFilter(EventFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();
        if (filter.getTitle() != null) {
            predicates.add(cb.like(cb.lower(event.get("title")), "%" + filter.getTitle().toLowerCase() + "%"));
        }
        if (filter.getEventType() != null) {
            predicates.add(cb.equal(event.get("type"), filter.getEventType()));
        }
        if (filter.getEventStatus() != null) {
            predicates.add(cb.equal(event.get("status"), filter.getEventStatus()));
        }
        if (filter.getStartFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(event.get("startDate"), filter.getStartFrom()));
        }
        if (filter.getStartTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(event.get("startDate"), filter.getStartTo()));
        }

        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }
}
