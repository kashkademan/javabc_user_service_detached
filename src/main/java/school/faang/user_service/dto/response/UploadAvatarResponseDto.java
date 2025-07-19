package school.faang.user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadAvatarResponseDto {
    private String fileId;
    private String smallFileId;
}
