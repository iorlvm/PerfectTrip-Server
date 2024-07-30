package idv.tia201.g1.converter;

import idv.tia201.g1.constant.Gender;
import idv.tia201.g1.constant.UserGroup;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserGroupConverter implements AttributeConverter<UserGroup, String> {

    @Override
    public String convertToDatabaseColumn(UserGroup attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public UserGroup convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return UserGroup.valueOf(dbData.toUpperCase());
    }

}