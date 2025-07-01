package school.faang.user_service.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

@Converter(autoApply = true)
public class LocaleAttributeConverter implements AttributeConverter<Locale, String> {

    @Override
    public String convertToDatabaseColumn(Locale locale) {
        return locale == null ? Locale.ENGLISH.toString() : locale.toString();
    }

    @Override
    public Locale convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Locale.ENGLISH;
        }

        String[] parts = dbData.split("_");
        return switch (parts.length) {
            case 1 -> new Locale(parts[0]);
            case 2 -> new Locale(parts[0], parts[1]);
            case 3 -> new Locale(parts[0], parts[1], parts[2]);
            default -> Locale.ENGLISH;
        };
    }
}