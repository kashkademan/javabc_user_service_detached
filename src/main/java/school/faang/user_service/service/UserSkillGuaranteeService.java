package school.faang.user_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

@Service
@RequiredArgsConstructor
public class UserSkillGuaranteeService {
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public void saveAll(List<UserSkillGuarantee> userSkillGuarantees) {
        userSkillGuaranteeRepository.saveAll(userSkillGuarantees);
    }
}
