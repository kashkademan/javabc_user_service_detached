package school.faang.user_service.mapper.contact;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.contact.ContactPreferenceResponseDto;
import school.faang.user_service.entity.contact.ContactPreference;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface ContactMapper {
    ContactPreferenceResponseDto toContactPreferenceResponseDto(ContactPreference contactPreference);

    List<ContactPreferenceResponseDto> toContactPreferenceResponseDtoList(List<ContactPreference> contactPreferences);
}
