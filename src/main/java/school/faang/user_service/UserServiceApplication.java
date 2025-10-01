package school.faang.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
@EnableFeignClients("school.faang.user_service.client")
public class UserServiceApplication implements CommandLineRunner {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        subscriptionRepository.followUser(1L, 2L);
        log.info("Тест пройден: подписка 1 -> 2 создана");

        try {
            followUser(1L, 1L);
            log.error("Ошибка: подписка на себя прошла");
        } catch (DataValidationException e) {
            log.info("Тест пройден: " + e.getMessage());
        }
    }

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписаться на себя");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Уже подписаны");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }
}