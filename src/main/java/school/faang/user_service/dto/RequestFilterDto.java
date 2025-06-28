package school.faang.user_service.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
public class RequestFilterDto {
    @Size(max = 255)
    private String descriptionPattern;
    private RequestStatus statusPattern;
    private Long requesterIdPattern;
    private Long receiverIdPattern;
}