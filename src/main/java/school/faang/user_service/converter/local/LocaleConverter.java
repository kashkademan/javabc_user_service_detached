package school.faang.user_service.converter.local;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

@Converter
public class LocaleConverter implements AttributeConverter<Locale, String> {

    @Override
    public String convertToDatabaseColumn(Locale locale) {
        return locale == null ? null : locale.toString();
    }

    @Override
    public Locale convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Locale.forLanguageTag(dbData.replace('_', '-'));
    }
}
