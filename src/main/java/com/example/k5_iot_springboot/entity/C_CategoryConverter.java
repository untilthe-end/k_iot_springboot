package com.example.k5_iot_springboot.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// 자바에서 제공하는 기능 Converter                            // C_Category 값을 String으로 바꾸고싶어

@Converter(autoApply = false)
public class C_CategoryConverter implements AttributeConverter<C_Category, String> {
    @Override
    public String convertToDatabaseColumn(C_Category cCategory) {
        return cCategory == null ? null : cCategory.getDbValue();
    }

    @Override
    public C_Category convertToEntityAttribute(String s) {
        return C_Category.fromDbValue(s);
    }
}
