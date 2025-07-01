package school.faang.user_service.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class TestContainersConfig {

    public static final String POSTGRES_IMAGE = "postgres:13.3";
    public static final String MINIO_IMAGE = "minio/minio:latest";
    public static final String MINIO_USER = "user";
    public static final String MINIO_PASSWORD = "password";
    public static final String MINIO_COMMAND = "server /data";
    private static final String BUCKET = "corpbucket";

    public static final int MINIO_PORT = 9000;

    @Container
    public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"))
            .withReuse(true);

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("postgres")
            .withUsername("user")
            .withPassword("password");

    @Container
    public static final GenericContainer<?> MINIO = new GenericContainer<>(MINIO_IMAGE)
            .withEnv("MINIO_ROOT_USER", MINIO_USER)
            .withEnv("MINIO_ROOT_PASSWORD", MINIO_PASSWORD)
            .withCommand(MINIO_COMMAND)
            .withExposedPorts(MINIO_PORT);

    @Container
    public static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        registry.add("spring.liquibase.url", POSTGRES::getJdbcUrl);
        registry.add("spring.liquibase.user", POSTGRES::getUsername);
        registry.add("spring.liquibase.password", POSTGRES::getPassword);

        registry.add("cloud.aws.s3.endpoint", () ->
                "http://" + MINIO.getHost() + ":" + MINIO.getMappedPort(9000));

        registry.add("cloud.aws.s3.access-key", () -> MINIO_USER);
        registry.add("cloud.aws.s3.secret-key", () -> MINIO_PASSWORD);
        registry.add("cloud.aws.s3.bucket", () -> BUCKET);

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));

        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }
}
