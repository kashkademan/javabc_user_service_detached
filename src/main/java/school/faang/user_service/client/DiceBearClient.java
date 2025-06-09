package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "dice-bear",
        url = "https://${services.dice-bear.domain}/${services.dice-bear.version}",
        configuration = FeignConfig.class)
public interface DiceBearClient {
    @GetMapping(value = "/pixel-art/svg", produces = "image/svg+xml")
    byte[] getRandomAvatar(@RequestParam(required = false) String seed);

    @GetMapping(value = "/pixel-art/svg", produces = "image/svg+xml")
    byte[] getRandomAvatar();
}
