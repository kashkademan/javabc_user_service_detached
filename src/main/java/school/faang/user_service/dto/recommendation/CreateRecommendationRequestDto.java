package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecommendationRequestDto {

    @NotNull(message = "Message must not be null")
    private String message;

    @NotNull(message = "ReceiverId must not be null")
    private Long receiverId;

    private List<Long> skillIds;
}
