package school.faang.user_service.client;

import feign.Logger;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiceBearClientConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Retryer feignRetryer() {
        return new DiceBearRetryer();
    }


    // Попытка прописать свой RequestInterceptor и обойти дефолтный, как найду решение, вернусь к этому
    /*@Bean
    public RequestInterceptor diceBearClientRequestInterceptor() {
        return template -> {
            template.removeHeader("Accept");
        };
    }

    @Bean
    public List<RequestInterceptor> diceBearClientRequestInterceptors() {
        return List.of(diceBearClientRequestInterceptor());
    }*/
}