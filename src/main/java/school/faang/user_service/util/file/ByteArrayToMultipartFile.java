package school.faang.user_service.util.file;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ByteArrayToMultipartFile implements Converter<byte[], MultipartFile> {
    @Override
    public MultipartFile convert(@NotNull byte[] source) {
        return new CustomMultipartFile(
                source,
                "file",
                "file",
                null
        );
    }
}