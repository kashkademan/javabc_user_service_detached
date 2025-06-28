package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionErrorResponse {
    private String message;
    private long timestamp;
}
