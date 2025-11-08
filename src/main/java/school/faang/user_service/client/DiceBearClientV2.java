package school.faang.user_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class DiceBearClientV2 {

    @Value("${dice.bear.client.baseUrl}")
    private String baseUrl;

    private final WebClient webClient;

    public CompletableFuture<byte[]> generateAvatarPng(String style) {
        String url = String.format("%s/%s/png", baseUrl, style);
        log.debug("Generating avatar from DiceBear: {}", url);

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        clientResponse -> {
                            log.warn("DiceBear returned HTTP {} for URL {}", clientResponse.statusCode(), url);
                            return Mono.error(new RuntimeException(
                                    "DiceBear API returned error: " + clientResponse.statusCode()));
                        })
                .bodyToMono(byte[].class)
                .doOnError(e -> log.error(
                        "Failed to fetch avatar from DiceBear (URL: {}): {}", url, e.getMessage())).toFuture();
    }
}