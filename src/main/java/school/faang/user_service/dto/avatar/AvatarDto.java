package school.faang.user_service.dto.avatar;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvatarDto {
    private String avatarUrl;
    private String seed;
    private Long userId;
}