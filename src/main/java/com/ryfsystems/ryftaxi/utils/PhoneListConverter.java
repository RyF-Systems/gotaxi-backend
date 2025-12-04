package com.ryfsystems.ryftaxi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryfsystems.ryftaxi.dto.Phone;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Converter(autoApply = true)
@Component
public class PhoneListConverter implements AttributeConverter<List<Phone>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Phone> phones) {
        if (phones == null || phones.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(phones);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting phones to JSON", e);
        }
    }

    @Override
    public List<Phone> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty() || dbData.equals("[]")) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Phone>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to phones", e);
        }
    }
}