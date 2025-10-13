package school.faang.user_service.service.career;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.user.CareerRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CareerServiceTest_v2 {

  private static final Long USER_ID = 10L;
  private static final Long CAREER_ID = 15L;
  private static final String COMPANY = "company";
  private static final String POSITION = "position";
  private static final LocalDate START_DATE = LocalDate.of(2021, 6, 15);
  private static final LocalDate END_DATE = LocalDate.of(2023, 12, 31);

  private final User testUser = User.builder().id(USER_ID).build(); //Builder - No BeforeEach

  @Spy
  private final CareerMapper careerMapper = Mappers.getMapper(CareerMapper.class); // Correct option for create mapper

  @Mock
  private CareerRepository careerRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CareerServiceImpl careerService;

  @Test
  void addCareer_WithValidDataReturnsCareerDto() {
    CreateCareerDto createDto = new CreateCareerDto(START_DATE, END_DATE, COMPANY, POSITION); // Firstly variables
    Career career = Career.builder().id(CAREER_ID).dateFrom(START_DATE).dateTo(END_DATE).company(COMPANY).position(POSITION).build();

    when(userRepository.getByIdOrThrow(USER_ID)).thenReturn(testUser); // Then mock
    when(careerRepository.save(any(Career.class))).thenReturn(career);

    CareerDto result = careerService.addCareer(USER_ID, createDto); // Action

    assertEquals(COMPANY, result.company()); //Checks
    assertEquals(POSITION, result.position());
    assertEquals(END_DATE, result.to());
    assertEquals(START_DATE, result.from());
    assertEquals(CAREER_ID, result.id());

    verify(careerMapper).toCareer(createDto); //Verify
    verify(careerMapper).toCareerDto(career);
    verify(careerRepository).save(any(Career.class));
    verify(userRepository).getByIdOrThrow(USER_ID);
  }
}
