package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "dicebear-client", url = "${services.dicebear.url}")
public interface DiceBearClient {

    @GetMapping(value = "/{version}/{style}/{format}", produces = MediaType.IMAGE_PNG_VALUE)
    byte[] getAvatar(
            @PathVariable("version") String version,
            @PathVariable("style") String style,
            @PathVariable("format") String format,
            @RequestParam("seed") String seed

    );
}