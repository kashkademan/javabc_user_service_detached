package school.faang.user_service.service.team;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.team.TeamDto;
import school.faang.user_service.entity.team.Team;

import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.FileUploadException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.TeamMapper;
import school.faang.user_service.repository.team.TeamRepository;
import school.faang.user_service.service.image.ImageService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamServiceImpl Tests")
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private UserContext userContext;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamServiceImpl teamService;

    private Team team;
    private User manager;
    private TeamDto teamDto;
    private MultipartFile file;

    private static final long TEAM_ID = 1L;
    private static final long MANAGER_ID = 100L;
    private static final long OTHER_USER_ID = 200L;
    private static final String AVATAR_KEY = "team-1-avatar-test.jpg";
    private static final String NEW_AVATAR_KEY = "team-1-avatar-new.jpg";

    @BeforeEach
    void setUp() {
        manager = User.builder()
                .id(MANAGER_ID)
                .build();

        team = Team.builder()
                .id(TEAM_ID)
                .name("Test Team")
                .description("Test Description")
                .manager(manager)
                .avatarKey(AVATAR_KEY)
                .build();

        teamDto = new TeamDto(
                TEAM_ID,
                "Test Team",
                "Test Description",
                null,
                "/api/teams/" + TEAM_ID + "/avatar",
                null,
                null
        );

        file = new MockMultipartFile(
                "avatar",
                "test.jpg",
                "image/jpeg",
                "test image".getBytes()
        );
    }

    @Test
    @DisplayName("Should successfully upload avatar for team manager")
    void uploadAvatar_Success() throws IOException {
        when(teamRepository.getByIdOrThrow(TEAM_ID)).thenReturn(team);
        when(userContext.getUserId()).thenReturn(MANAGER_ID);
        when(imageService.uploadTeamAvatar(file, TEAM_ID)).thenReturn(NEW_AVATAR_KEY);
        when(teamRepository.save(team)).thenReturn(team);
        when(teamMapper.toTeamDto(team)).thenReturn(teamDto);

        TeamDto result = teamService.uploadAvatar(TEAM_ID, file);

        assertThat(result).isEqualTo(teamDto);
        assertThat(team.getAvatarKey()).isEqualTo(NEW_AVATAR_KEY);

        verify(teamRepository).getByIdOrThrow(TEAM_ID);
        verify(imageService).uploadTeamAvatar(file, TEAM_ID);
        verify(teamRepository).save(team);
        verify(teamMapper).toTeamDto(team);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user is not team manager")
    void uploadAvatar_UserNotManager() throws IOException {
        when(teamRepository.getByIdOrThrow(TEAM_ID)).thenReturn(team);
        when(userContext.getUserId()).thenReturn(OTHER_USER_ID);

        assertThatThrownBy(() -> teamService.uploadAvatar(TEAM_ID, file))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Only team manager can modify avatar");

        verify(imageService, never()).uploadTeamAvatar(any(), anyLong());
        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw FileUploadException when image service throws IOException")
    void uploadAvatar_ImageServiceThrowsIOException() throws IOException {
        when(teamRepository.getByIdOrThrow(TEAM_ID)).thenReturn(team);
        when(userContext.getUserId()).thenReturn(MANAGER_ID);
        when(imageService.uploadTeamAvatar(file, TEAM_ID))
                .thenThrow(new IOException("IO error"));

        assertThatThrownBy(() -> teamService.uploadAvatar(TEAM_ID, file))
                .isInstanceOf(FileUploadException.class)
                .hasMessageContaining("Failed to upload avatar for team " + TEAM_ID)
                .hasCause(new IOException("IO error"));

        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully delete avatar when team has avatar")
    void deleteAvatar_Success() {
        when(teamRepository.getByIdOrThrow(TEAM_ID)).thenReturn(team);
        when(userContext.getUserId()).thenReturn(MANAGER_ID);
        doNothing().when(imageService).deleteTeamAvatar(AVATAR_KEY);
        when(teamRepository.save(team)).thenReturn(team);

        teamService.deleteAvatar(TEAM_ID);

        assertThat(team.getAvatarKey()).isNull();

        verify(imageService).deleteTeamAvatar(AVATAR_KEY);
        verify(teamRepository).save(team);
    }

    @Test
    @DisplayName("Should not delete avatar when team has no avatar")
    void deleteAvatar_TeamHasNoAvatar() {
        team = Team.builder()
                .id(TEAM_ID)
                .name("Test Team")
                .manager(manager)
                .avatarKey(null)
                .build();

        when(teamRepository.getByIdOrThrow(TEAM_ID)).thenReturn(team);
        when(userContext.getUserId()).thenReturn(MANAGER_ID);

        teamService.deleteAvatar(TEAM_ID);

        verify(imageService, never()).deleteTeamAvatar(any());
        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user is not team manager for delete")
    void deleteAvatar_UserNotManager() {
        when(teamRepository.getByIdOrThrow(TEAM_ID)).thenReturn(team);
        when(userContext.getUserId()).thenReturn(OTHER_USER_ID);

        assertThatThrownBy(() -> teamService.deleteAvatar(TEAM_ID))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Only team manager can modify avatar");

        verify(imageService, never()).deleteTeamAvatar(any());
        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully get avatar")
    void getAvatar_Success() {
        byte[] expectedBytes = "avatar data".getBytes();
        when(teamRepository.getByIdOrThrow(TEAM_ID)).thenReturn(team);
        when(imageService.getTeamAvatar(AVATAR_KEY)).thenReturn(expectedBytes);

        byte[] result = teamService.getAvatar(TEAM_ID);

        assertThat(result).isEqualTo(expectedBytes);
        verify(imageService).getTeamAvatar(AVATAR_KEY);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when team has no avatar")
    void getAvatar_TeamHasNoAvatar() {
        team = Team.builder()
                .id(TEAM_ID)
                .name("Test Team")
                .manager(manager)
                .avatarKey(null)
                .build();

        when(teamRepository.getByIdOrThrow(TEAM_ID)).thenReturn(team);

        assertThatThrownBy(() -> teamService.getAvatar(TEAM_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Team " + TEAM_ID + " does not have an avatar");

        verify(imageService, never()).getTeamAvatar(any());
    }

    @Test
    @DisplayName("Should allow access for team manager")
    void checkManagerRights_Success() throws IOException {
        when(teamRepository.getByIdOrThrow(TEAM_ID)).thenReturn(team);
        when(userContext.getUserId()).thenReturn(MANAGER_ID);
        when(imageService.uploadTeamAvatar(file, TEAM_ID)).thenReturn(NEW_AVATAR_KEY);
        when(teamRepository.save(team)).thenReturn(team);
        when(teamMapper.toTeamDto(team)).thenReturn(teamDto);

        teamService.uploadAvatar(TEAM_ID, file);

        verify(teamRepository).getByIdOrThrow(TEAM_ID);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Should deny access for non-manager")
    void checkManagerRights_AccessDenied() {
        when(teamRepository.getByIdOrThrow(TEAM_ID)).thenReturn(team);
        when(userContext.getUserId()).thenReturn(OTHER_USER_ID);

        assertThatThrownBy(() -> teamService.uploadAvatar(TEAM_ID, file))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Only team manager can modify avatar")
                .hasMessageContaining("Current user: " + OTHER_USER_ID)
                .hasMessageContaining("team manager: " + MANAGER_ID);
    }
}