package school.faang.user_service.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Embeddable
public class UserProfilePic {
    @NotBlank(message = "Field cannot be blank")
    private String fileId;

    @NotBlank(message = "Field cannot be blank")
    private String smallFileId;
}