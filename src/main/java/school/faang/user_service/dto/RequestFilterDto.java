package school.faang.user_service.dto;

import school.faang.user_service.entity.RequestStatus;

public record RequestFilterDto(Long id, String description, Long requesterId, Long receiverId, RequestStatus status) {
}
