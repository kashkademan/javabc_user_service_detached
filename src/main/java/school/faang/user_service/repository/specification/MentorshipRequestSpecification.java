package school.faang.user_service.repository.specification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

@Component
@RequiredArgsConstructor
public class MentorshipRequestSpecification {

    public static Specification<MentorshipRequest> hashDescription(String description) {
        return (root, query, criteriaBuilder) ->
                description == null ? null : criteriaBuilder.equal(root.get("description"), description);
    }

    public static Specification<MentorshipRequest> hashRequesterId(Long requesterId) {
        return (root, query, criteriaBuilder) ->
                requesterId == null ? null : criteriaBuilder.equal(root.get("requester").get("id"), requesterId);
    }

    public static Specification<MentorshipRequest> hashReceiverId(Long receiverId) {
        return (root, query, criteriaBuilder) ->
                receiverId == null ? null : criteriaBuilder.equal(root.get("receiver").get("id"), receiverId);
    }

    public static Specification<MentorshipRequest> hasStatus(RequestStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<MentorshipRequest> buildFilter(RequestFilterDto filterDto) {
        return Specification
                .where(hasStatus(filterDto.getStatus()))
                .and(hashDescription(filterDto.getDescription()))
                .and(hashReceiverId(filterDto.getReceiverId()))
                .and(hashRequesterId(filterDto.getRequesterId()));
    }
}