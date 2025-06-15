package school.faang.user_service.service.career;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CareerServiceTest {

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private CareerMapper careerMapper = Mappers.getMapper(CareerMapper.class);

    @InjectMocks
    private CareerServiceImpl careerService;

    private CareerDto testCareerDto;
    private User testUser;

    @BeforeEach
    void setUpClass() {
        testCareerDto = new CareerDto();
        testCareerDto.setCompany("company");
        testCareerDto.setPosition("position");
        testCareerDto.setTo(LocalDate.now());
        testCareerDto.setFrom(LocalDate.now().minusDays(1));

        testUser = new User();
        testUser.setId(10L);
    }

    @Test
    public void testAddCareer_whenValidData_thenSaveAndReturnDto() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));

        Career testCareer = careerMapper.toCareer(testCareerDto);
        testCareer.setUser(testUser);
        testCareer.setId(99L);
        when(careerRepository.save(any(Career.class))).thenReturn(testCareer);

        CareerDto testDtoResul = careerService.addCareer(10L, testCareerDto);

        assertEquals(testCareerDto.getCompany(), testDtoResul.getCompany());
        assertEquals(testCareerDto.getPosition(), testDtoResul.getPosition());
        assertEquals(testCareerDto.getTo(), testDtoResul.getTo());
        assertEquals(testCareerDto.getFrom(), testDtoResul.getFrom());
        assertEquals(99L, testDtoResul.getId());

        verify(careerRepository, times(1)).save(any(Career.class));
        verify(userRepository, times(1)).findById(10L);
    }

    @Test
    public void testAddCareer_whenFromDateInFuture_thenThrowsValidation() {
        testCareerDto.setFrom(LocalDate.now().plusDays(1));
        assertThrows(DataValidationException.class, () -> careerService.addCareer(10L, testCareerDto));

        verifyNoInteractions(userRepository, careerRepository, careerMapper);
    }

    @Test
    public void testUpdateCareer_whenValidData_thenSaveAndReturnDto() {
        testCareerDto.setId(5L);

        Career careerFromRepository = new Career();
        careerFromRepository.setId(5L);
        careerFromRepository.setUser(testUser);
        when(careerRepository.findById(testCareerDto.getId())).thenReturn(Optional.of(careerFromRepository));

        Career updatedCareer = careerMapper.toCareer(testCareerDto);
        updatedCareer.setUser(testUser);
        updatedCareer.setId(5L);
        when(careerRepository.save(any(Career.class))).thenReturn(updatedCareer);

        CareerDto savedCareer = careerService.updateCareer(10L, testCareerDto);

        assertEquals(testCareerDto.getCompany(), savedCareer.getCompany());
        assertEquals(testCareerDto.getPosition(), savedCareer.getPosition());
        assertEquals(testCareerDto.getTo(), savedCareer.getTo());
        assertEquals(testCareerDto.getFrom(), savedCareer.getFrom());
        assertEquals(savedCareer.getId(), updatedCareer.getId());
        verify(careerRepository, times(1)).save(any(Career.class));
        verify(careerRepository, times(1)).findById(5L);
    }

    @Test
    public void testUpdateCareer_whenCareerNotFound_thenThrowsNotFound() {
        testCareerDto.setId(99L);
        when(careerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> careerService.updateCareer(testUser.getId(), testCareerDto));
        verifyNoInteractions(userRepository, careerMapper);
    }

    @Test
    public void testGetById_whenValidData_thenReturnDto() {
        Career career = careerMapper.toCareer(testCareerDto);
        career.setUser(testUser);
        career.setId(99L);
        when(careerRepository.findById(99L)).thenReturn(Optional.of(career));

        CareerDto careerDto = careerService.getById(99L);

        assertEquals(careerDto.getCompany(), career.getCompany());
        assertEquals(careerDto.getPosition(), career.getPosition());
        assertEquals(careerDto.getFrom(), career.getDateFrom());
        assertEquals(careerDto.getTo(), career.getDateTo());
        assertEquals(careerDto.getId(), career.getId());
        verify(careerRepository, times(1)).findById(99L);
    }

    @Test
    public void testGetById_thenInvalidData_whenThrowsNotFound() {
        when(careerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> careerService.getById(99L));
        verifyNoInteractions(userRepository, careerMapper);
    }
}