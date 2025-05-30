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

    // TODO: не уверен что нужен первый метод
    public List<Event> findByFilter(EventFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);

        PredicateBuilder builder = new PredicateBuilder();
        builder
                .add(filter.getTitle() == null ? null : cb.like(cb.lower(event.get("title")), "%" + filter.getTitle().toLowerCase() + "%"))
                .add(filter.getEventType() == null ? null : cb.equal(event.get("type"), filter.getEventType()))
                .add(filter.getEventStatus() == null ? null : cb.equal(event.get("status"), filter.getEventStatus()))
                .add(filter.getStartFrom() == null ? null : cb.greaterThanOrEqualTo(event.get("startDate"), filter.getStartFrom()))
                .add(filter.getStartTo() == null ? null : cb.lessThanOrEqualTo(event.get("startDate"), filter.getStartTo()));

        query.where(builder.build());
        return entityManager.createQuery(query).getResultList();
    }

    public List<Event> findByFilter(EventFilter filter, List<Long> eventIds) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);

        PredicateBuilder builder = new PredicateBuilder();
        builder
                .add(filter.getTitle() == null ? null : cb.like(cb.lower(event.get("title")), "%" + filter.getTitle().toLowerCase() + "%"))
                .add(filter.getEventType() == null ? null : cb.equal(event.get("type"), filter.getEventType()))
                .add(filter.getEventStatus() == null ? null : cb.equal(event.get("status"), filter.getEventStatus()))
                .add(filter.getStartFrom() == null ? null : cb.greaterThanOrEqualTo(event.get("startDate"), filter.getStartFrom()))
                .add(filter.getStartTo() == null ? null : cb.lessThanOrEqualTo(event.get("startDate"), filter.getStartTo()))
             // TODO: проверить
                .add(cb.not(event.get("id").in(eventIds)));

        query.where(builder.build());
        return entityManager.createQuery(query).getResultList();
    }

    private static class PredicateBuilder {
        private final List<Predicate> predicates = new ArrayList<>();

        public PredicateBuilder add(Predicate p) {
            if (p != null) {
                predicates.add(p);
            }
            return this;
        }

        public Predicate[] build() {
            return predicates.toArray(new Predicate[0]);
        }
    }
}
