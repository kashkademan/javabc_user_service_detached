package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
public class RequestFilterDto {
    private String descriptionPattern;
    private RequestStatus statusPattern;
    private Long requesterIdPattern;
    private Long receiverIdPattern;
}