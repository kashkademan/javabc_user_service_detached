package school.faang.user_service.filter.MentorshipRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
public class DescriptionFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getDescription() != null && !requestFilterDto.getDescription().isBlank();
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequestStream, RequestFilterDto requestFilterDto) {
        String filterDescription = requestFilterDto.getDescription();
        if (filterDescription == null || filterDescription.isBlank()) {
            return mentorshipRequestStream;
        }

        // Разбиваем описание фильтра на слова >= 3 символов, приводим к нижнему регистру
        List<String> filterWords = Arrays.stream(filterDescription.split("\\s+"))
                .map(String::toLowerCase)
                .filter(word -> word.length() >= 3)
                .toList();

        return mentorshipRequestStream.filter(request -> {
            String requestDesc = request.getDescription();
            if (requestDesc == null || requestDesc.isBlank()) {
                return false;
            }
            String lowerRequestDesc = requestDesc.toLowerCase();

            // Проверяем, что хотя бы одно слово из фильтра есть в описании запроса
            return filterWords.stream().anyMatch(lowerRequestDesc::contains);
        });
    }

}
