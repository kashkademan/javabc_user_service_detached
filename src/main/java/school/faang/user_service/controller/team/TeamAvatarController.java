package school.faang.user_service.controller.team;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.team.TeamDto;
import school.faang.user_service.service.team.TeamService;

@RestController
@RequestMapping("/api/teams/{teamId}/avatar")
@RequiredArgsConstructor
public class TeamAvatarController {

    private final TeamService teamService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TeamDto uploadAvatar(@PathVariable long teamId,
                                @RequestParam("file") MultipartFile file) {
        return teamService.uploadAvatar(teamId, file);
    }

    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getAvatar(@PathVariable long teamId) {
        return teamService.getAvatar(teamId);
    }

    @DeleteMapping
    public void deleteAvatar(@PathVariable long teamId) {
        teamService.deleteAvatar(teamId);
    }
}