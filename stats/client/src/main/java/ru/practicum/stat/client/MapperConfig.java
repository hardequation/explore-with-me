package ru.practicum.stat.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class MapperConfig {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(FORMAT);
        javaTimeModule.addSerializer(LocalDateTime.class, localDateTimeSerializer);

        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(FORMAT);
        javaTimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);

        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }
}

