package school.faang.user_service.dto.csv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsvUploadResponseDto {
    private int totalStudents;
    private int processedCount;
    private int errorCount;
    private List<String> errors;
}