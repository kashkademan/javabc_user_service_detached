package school.faang.user_service.entity.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkillOfferEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long requesterId;
    private Long receiverId;
    private Long skillOfferId;
}
