package school.faang.user_service.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Embeddable
public class UserProfilePic {
    @NotBlank(message = "Field cannot be blank")
    @Size(max = 255, message = "Field length must be less or equal 255")
    private String fileId;

    @NotBlank(message = "Field cannot be blank")
    @Size(max = 255, message = "Field length must be less or equal 255")
    private String smallFileId;
}