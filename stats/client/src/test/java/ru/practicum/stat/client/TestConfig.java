package ru.practicum.stat.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@TestConfiguration
public class TestConfig {

    @Bean
    public ObjectMapper objectMapper() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(formatter);
        javaTimeModule.addSerializer(LocalDateTime.class, localDateTimeSerializer);

        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(formatter);
        javaTimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);

        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }
}

