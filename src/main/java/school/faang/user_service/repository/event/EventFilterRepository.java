package school.faang.user_service.repository.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.model.event.EventFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Repository
public class EventFilterRepository {
    @PersistenceContext
    private EntityManager entityManager;
    public List<Long> findByFilter(EventFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Event> event = query.from(Event.class);

        Join<Event, Promotion> promotionJoin = event.join("promotions", JoinType.LEFT);
        promotionJoin.on(cb.equal(promotionJoin.get("status"), PromotionStatus.ACTIVE));

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

        query.select(event.get("id"));
        query.where(builder.build());

        Expression<Integer> priority = cb.coalesce(
                promotionJoin.get("tariff").get("coefficientPriority"),
                Integer.MAX_VALUE
        );

        query.orderBy(cb.asc(priority));

        return entityManager.createQuery(query).getResultList();
    }

    private static class PredicateBuilder {
        private final Root<Event> root;
        private final List<Predicate> predicates = new ArrayList<>();

        public PredicateBuilder(Root<Event> root) {
            this.root = root;
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
