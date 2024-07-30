package idv.tia201.g1.converter;

import idv.tia201.g1.constant.Action;
import idv.tia201.g1.constant.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ActionConverter implements AttributeConverter<Action, String> {

    @Override
    public String convertToDatabaseColumn(Action attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public Action convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Action.valueOf(dbData.toUpperCase());
    }

}
