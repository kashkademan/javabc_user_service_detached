package school.faang.user_service;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ApplicationContextTest {
    private static final String REDIS_PASSWORD = "testContainerRedis";

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"))
                    .withCommand("redis-server --requirepass %s".formatted(REDIS_PASSWORD));

    @Container
    static final MinIOContainer MINIO_CONTAINER =
            new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z")
                    .withUserName("user")
                    .withPassword("password");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.start();
        MINIO_CONTAINER.start();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.password", () -> REDIS_PASSWORD);

        registry.add("redis.topics.name.user-ban-topic", () -> "user-ban-topic-test");

        registry.add("services.minio.endpoint", MINIO_CONTAINER::getS3URL);
        registry.add("services.minio.accessKey", MINIO_CONTAINER::getUserName);
        registry.add("services.minio.secretKey", MINIO_CONTAINER::getPassword);
    }

    @Test
    void contextLoads() {

    }
}
