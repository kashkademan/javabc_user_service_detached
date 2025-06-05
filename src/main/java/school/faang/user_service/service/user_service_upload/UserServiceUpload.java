package school.faang.user_service.service.user_service_upload;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.csv.CsvUploadResponseDto;

public interface UserServiceUpload {
    CsvUploadResponseDto processStudentsCsv(MultipartFile file);
}
