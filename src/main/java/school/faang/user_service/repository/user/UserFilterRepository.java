package school.faang.user_service.repository.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.country.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.model.user.UserFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Repository
public class UserFilterRepository {
    @PersistenceContext
    private EntityManager entityManager;
    public List<Long> findByFilter(UserFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<User> user = query.from(User.class);

        Join<User, Country> countryJoin = user.join("country", JoinType.LEFT);

        UserFilterRepository.PredicateBuilder builder = new UserFilterRepository.PredicateBuilder(user);
        builder
                .addIfNotNull(filter.getUsername(), root ->
                        cb.like(cb.lower(root.get("username")), "%" + filter.getUsername().toLowerCase() + "%"))
                .addIfNotNull(filter.getEmail(), root ->
                        cb.like(cb.lower(root.get("email")), "%" + filter.getEmail().toLowerCase() + "%"))
                .addIfNotNull(filter.getPhone(), root ->
                        cb.like(root.get("phone"), "%" + filter.getPhone() + "%"))
                .addIfNotNull(filter.getAboutMe(), root ->
                        cb.like(cb.lower(root.get("aboutMe")), "%" + filter.getAboutMe().toLowerCase() + "%"))
                .addIfNotNull(filter.getCountry(), (root) ->
                        cb.like(cb.lower(countryJoin.get("title")), "%" + filter.getCountry().toLowerCase() + "%"))
                .addIfNotNull(filter.getCity(), root ->
                        cb.like(cb.lower(root.get("city")), "%" + filter.getCity().toLowerCase() + "%"))
                .addIfNotNull(filter.getMinExperience(), root ->
                        cb.greaterThanOrEqualTo(root.get("experience"), filter.getMinExperience()));

        query.select(user.get("id"));
        query.where(builder.build());

        return entityManager.createQuery(query).getResultList();
    }

    private static class PredicateBuilder {
        private final Root<User> root;
        private final List<Predicate> predicates = new ArrayList<>();

        public PredicateBuilder(Root<User> root) {
            this.root = root;
        }
        public UserFilterRepository.PredicateBuilder addIfNotNull(Object value, Function<Root<User>, Predicate> fn) {
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
