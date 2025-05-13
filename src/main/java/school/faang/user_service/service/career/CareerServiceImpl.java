package school.faang.user_service.service.career;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CareerServiceImpl implements CareerService {
    private final CareerRepository careerRepository;
    private final UserRepository userRepository;
    private final CareerMapper careerMapper;

    @Override
    public CareerDto addCareer(long userId, CareerDto careerDto) {
        validateFromDate(careerDto.getFrom());
        User user = userRepository.findById(userId).orElseThrow(() ->
                new DataValidationException("User not found"));
        Career career = careerMapper.toCareer(careerDto);
        career.setUser(user);
        Career savedCareer = careerRepository.save(career);
        return careerMapper.toCareerDto(savedCareer);
    }

    @Override
    public CareerDto updateCareer(long userId, CareerDto careerDto) {
        validateFromDate(careerDto.getFrom());
        Career existingCareer = careerRepository.findById(careerDto.getId())
                .orElseThrow(() -> new DataValidationException("Career not found"));
        if (!existingCareer.getUser().getId().equals(userId)) {
            throw new DataValidationException("Career user id not match");
        }
        Career careerToSave = careerMapper.toCareer(careerDto);
        careerToSave.setId(existingCareer.getId());
        careerToSave.setUser(existingCareer.getUser());
        Career savedCareer = careerRepository.save(careerToSave);
        return careerMapper.toCareerDto(savedCareer);
    }

    @Override
    public CareerDto getById(long careerId) {
        return careerRepository.findById(careerId)
                .map(careerMapper::toCareerDto)
                .orElseThrow(() -> new RuntimeException("Career not found"));
    }

    private void validateFromDate(LocalDate fromDate) {
        if (!fromDate.isBefore(LocalDate.now())) {
            throw new DataValidationException("Career date cannot be in the future");
        }
    }
}