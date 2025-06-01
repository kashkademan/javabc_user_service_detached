package school.faang.user_service.dto.avatar;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvatarDto {
    private String avatarUrl;
    private String seed;
    private Long userId;
}