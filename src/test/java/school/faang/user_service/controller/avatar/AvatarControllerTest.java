package school.faang.user_service.controller.avatar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.avatar.AvatarDownloadDto;
import school.faang.user_service.service.avatar.AvatarService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для {@link AvatarController}.
 *
 * @author Linempy
 * @since 06.08.2025
 */
@WebMvcTest(AvatarController.class)
class AvatarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AvatarService avatarService;

    @MockBean
    private UserContext userContext;

    @Test
    @DisplayName("GET /avatars - должен возвращать 204 No Content при успешной генерации аватара")
    void testGenerateAvatarShouldReturnNoContent() throws Exception {
        doNothing().when(avatarService).generateAndSaveAvatar();

        mockMvc.perform(get("/avatars"))
                .andExpect(status().isNoContent());

        verify(avatarService).generateAndSaveAvatar();
    }

    @Test
    @DisplayName("POST /avatars - должен возвращать данные аватара")
    void testDownloadAvatar_ShouldReturnAvatarData() throws Exception {
        AvatarDownloadDto mockDto = new AvatarDownloadDto(
                new byte[]{1, 2, 3},
                "image/png",
                "avatar.png",
                3
        );

        when(avatarService.downloadAvatar()).thenReturn(mockDto);

        mockMvc.perform(post("/avatars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentType").value("image/png"))
                .andExpect(jsonPath("$.filename").value("avatar.png"))
                .andExpect(jsonPath("$.fileSize").value(3));
    }
}