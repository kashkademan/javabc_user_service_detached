package school.faang.user_service.client.dice_bear;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.util.retry.Retry;
import school.faang.user_service.config.client.web.dice_bear.DiceBearConfigurationProperties;

import java.time.Duration;
import java.util.UUID;

@Component
@Slf4j
public class DiceBearClient {
    private final WebClient webClient;

    public DiceBearClient(WebClient.Builder builder,
                              DiceBearConfigurationProperties props) {
        this.webClient = builder
                .baseUrl(props.getUrl() + "/" + props.getVersion())
                .build();
    }

    public byte[] getRandomAvatar(MediaType type) {
        String randomSeed = UUID.randomUUID().toString();
        String uri = UriComponentsBuilder
                .fromPath("/pixel-art/svg")
                .queryParam("seed", randomSeed)
                .build()
                .toUriString();

        log.debug("Dice bear client sent request get random avatar with random seed {}", randomSeed);

        byte[] response = webClient
                .get()
                .uri(uri)
                .accept(type)
                .retrieve()
                .bodyToMono(byte[].class)
                .retryWhen(Retry.backoff(5, Duration.ofMillis(2)))
                .block();

        log.debug("Dice bear client got response get random avatar SVG({} bytes)", response);

        return response;
    }
}
