package school.faang.user_service.dto.minios3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String fileId;
    private String fileName;
    private String fileUrl;
    private long size;
    private String contentType;
    private LocalDateTime uploadTime;
}

