//package ru.practicum.ewm.statistics.client;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import ru.practicum.ewm.statistics.dto.CreateEndpointHitDto;
//import ru.practicum.ewm.statistics.dto.EndpointHitDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = StatisticsController.class)
//@ContextConfiguration(classes = TestConfig.class)
//class StatisticsControllerTest {
//
//    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper mapper;
//
//    @MockBean
//    private StatisticsClient client;
//
//    private EndpointHitDto requestDto;
//
//    @BeforeEach
//    void setUp() {
//
//
//    }
//
//    @Test
//    void testCreateRecord() throws Exception {
//        CreateEndpointHitDto requestDto = CreateEndpointHitDto.builder()
//                .app("test-app")
//                .uri("/test-uri")
//                .ip("192.168.0.1")
//                .timestamp(LocalDateTime.parse("2025-01-12 22:09:09", FORMATTER))
//                .build();
//
//        EndpointHitDto resultRequestDto = EndpointHitDto.builder()
//                .app("some App")
//                .uri("/some/endpoint")
//                .ip("2.235.127.205")
//                .timestamp(LocalDateTime.parse("2022-07-03 19:55:00", FORMATTER))
//                .build();
//
//        when(client.createRecord(any()))
//                .thenReturn(ResponseEntity.of(Optional.of(resultRequestDto)));
//
//        mockMvc.perform(post("/hit")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(requestDto)))
//                        .andExpect(status().isOk())
//                        .andDo(print());
//    }
//
////    @Test
////    void successCreateRequest() throws Exception {
////        CreateEndpointHitDto requestDto = CreateEndpointHitDto.builder()
////                .app("some App")
////                .uri("/some/endpoint")
////                .ip("2.235.127.205")
////                .timestamp(LocalDateTime.parse("2022.07.03 19:55:00", formatter))
////                .build();
////
////        EndpointHitDto resultDto = EndpointHitDto.builder()
////                .app("some App")
////                .uri("/some/endpoint")
////                .ip("2.235.127.205")
////                .timestamp(LocalDateTime.parse("2022.07.03 19:55:00", formatter))
////                .build();
////
////        when(client.createRecord(requestDto))
////                .thenReturn(resultDto);
////
////        mvc.perform(get("/users")
////                        .content(mapper.writeValueAsString(userDto))
////                        .characterEncoding(StandardCharsets.UTF_8)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .accept(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
////                .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
////                .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
////                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
////    }
//}