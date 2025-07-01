package school.faang.user_service.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.dto.user.UserDto;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
public class ProfileViewDto {
    private UserDto profileUser;
    private UserDto viewerUser;
    private LocalDateTime viewingTime;
}
