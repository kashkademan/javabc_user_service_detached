package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedDto {
    private Long userId;
    private String username;
    private Integer experience;
    private String pictureFileId;
}
