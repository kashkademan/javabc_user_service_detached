package school.faang.user_service.filter.recommendation;

import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;


public class RecommendationRequestSpecification {

    public static Specification<RecommendationRequest> withFilters(RecommendationRequestFilterDto filterDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterDto.requesterId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("requester").get("id"), filterDto.requesterId()));
            }

            if (filterDto.receiverId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("receiver").get("id"), filterDto.receiverId()));
            }

            if (filterDto.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filterDto.status()));
            }

            if (filterDto.messageContains() != null && !filterDto.messageContains().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("message")),
                        "%" + filterDto.messageContains().toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
