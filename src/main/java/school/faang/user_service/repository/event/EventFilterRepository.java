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
import java.util.function.Function;

@Repository
public class EventFilterRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Event> findByFilter(EventFilter filter, List<Long> eventIds) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> event = query.from(Event.class);

        PredicateBuilder builder = new PredicateBuilder(event);
        builder
                .addIfNotNull(filter.getTitle(), root ->
                        cb.like(cb.lower(root.get("title")), "%" + filter.getTitle().toLowerCase() + "%"))
                .addIfNotNull(filter.getEventType(), root ->
                        cb.equal(root.get("type"), filter.getEventType()))
                .addIfNotNull(filter.getEventStatus(), root ->
                        cb.equal(root.get("status"), filter.getEventStatus()))
                .addIfNotNull(filter.getStartFrom(), root ->
                        cb.greaterThanOrEqualTo(root.get("startDate"), filter.getStartFrom()))
                .addIfNotNull(filter.getStartTo(), root ->
                        cb.lessThanOrEqualTo(root.get("startDate"), filter.getStartTo()));

        if (eventIds != null && !eventIds.isEmpty()) {
            builder.add(root -> cb.not(root.get("id").in(eventIds)));
        }

        query.select(event).where(builder.build());
        return entityManager.createQuery(query).getResultList();
    }

    private static class PredicateBuilder {
        private final Root<Event> root;
        private final List<Predicate> predicates = new ArrayList<>();

        public PredicateBuilder(Root<Event> root) {
            this.root = root;
        }

        public PredicateBuilder add(Function<Root<Event>, Predicate> fn) {
            predicates.add(fn.apply(root));
            return this;
        }

        public PredicateBuilder addIfNotNull(Object value, Function<Root<Event>, Predicate> fn) {
            if (value != null) {
                predicates.add(fn.apply(root));
            }
            return this;
        }

        public Predicate[] build() {
            return predicates.toArray(new Predicate[0]);
        }
    }
}
