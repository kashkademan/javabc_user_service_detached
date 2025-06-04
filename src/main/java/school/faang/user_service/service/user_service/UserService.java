package school.faang.user_service.service.user_service;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.csv.CsvUploadResponseDto;

public interface UserService {
    CsvUploadResponseDto processStudentsCsv(MultipartFile file);
}
