package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.user.EducationRepository;
import school.faang.user_service.service.education.EducationService;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class EducationServiceIntegrationTest {

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private EducationService educationService;

    @Autowired
    private UserContext userContext;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.2")
            .withDatabaseName("faang")
            .withUsername("faang")
            .withPassword("faang");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeEach
    void init() {
        userContext.setUserId(1L);
    }

    @Test
    public void getEducationByIdTest() {
        Education education = createEducation();
        educationRepository.save(education);
        Optional<Education> educationById = educationRepository.findById(education.getId());
        Assertions.assertNotNull(educationById.get());
        Assertions.assertEquals(educationById.get().getId(), education.getId());
        Assertions.assertEquals(educationById.get().getYearFrom(), education.getYearFrom());
        Assertions.assertEquals(educationById.get().getYearTo(), education.getYearTo());
    }

    @Test
    public void saveEducationTest_forbidden() {
        EducationDto educationDto = createEducationDto();
        Assertions.assertThrows(ForbiddenException.class, () -> educationService.addEducation(2L, educationDto));
    }

    private Education createEducation() {
        Education education = new Education(1L, 2000, 2010, "university",
                "middle", "spec1", new User(1L, "user1"));
        return education;
    }

    private EducationDto createEducationDto() {
        EducationDto educationDto = new EducationDto(2000, 2007, "university",
                "middle", "spec2");
        return educationDto;
    }
}
