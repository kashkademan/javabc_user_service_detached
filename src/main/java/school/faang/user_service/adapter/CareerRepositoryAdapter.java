package school.faang.user_service.adapter;

import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.entity.Career;
import school.faang.user_service.repository.CareerRepository;

public class CareerRepositoryAdapter {

    public static Career CareerFromRepository (CareerRepository careerRepository, long careerId) {
        return careerRepository.findById(careerId)
                .orElseThrow(() -> new EntityNotFoundException("Career with id " + careerId + " not found"));
    }
}
