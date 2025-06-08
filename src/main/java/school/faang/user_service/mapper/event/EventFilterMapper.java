package school.faang.user_service.mapper.event;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventFilterRequestDto;
import school.faang.user_service.model.event.EventFilter;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface EventFilterMapper {
    EventFilter toFilter(EventFilterRequestDto dto);
}
