package school.faang.user_service.dto;

import lombok.Data;

@Data
public class RegisterParticipantRequestDto {
    private Long id;
    private String username;
    private String email;
}