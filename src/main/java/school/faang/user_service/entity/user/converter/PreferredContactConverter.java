package school.faang.user_service.entity.user.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import school.faang.user_service.entity.contact.PreferredContact;

@Converter(autoApply = true)
public class PreferredContactConverter implements AttributeConverter<PreferredContact, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PreferredContact attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public PreferredContact convertToEntityAttribute(Integer dbData) {
        return PreferredContact.fromCode(dbData);
    }
}