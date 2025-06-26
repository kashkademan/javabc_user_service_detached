package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserFullDto(
        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Phone cannot be blank")
        String phone,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 100)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // пароль только для записи (не будет в ответе API)
        String password,

        String aboutMe,

        @NotNull(message = "Country cannot be blank")
        Long countryId,

        String city,

        @NotNull(message = "Experience cannot be null")
        Integer experience,

        String defaultPhoto
) {
}
