package school.faang.user_service.dto;

import school.faang.user_service.entity.RequestStatus;

public record RequestFilterDto(String description, Long requesterId, Long receiverId, RequestStatus status) {
}
