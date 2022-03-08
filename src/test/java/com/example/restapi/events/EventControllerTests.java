package com.example.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest // @SpringBootApplication 하위의 Bean들을 찾아서 등록해줌(Web app과 가장 유사한 형태가 됨)
@AutoConfigureMockMvc
// @WebMvcTest // 슬라이스용 테스트라 Web용 빈들만 등록 해줌(repository는 등록해주지 않음
public class EventControllerTests {

  // 웹과 관련한 빈들만 등록 -> 단위 테스트라고 보기는 어려움 -dispatcher(핸들러, 매퍼, 컨버터) eventcontroller...
  @Autowired
  MockMvc mockMvc; // 디스패치 서블릿은 만들지만 웹서버를 띄우지 않아 빠름(단위T 보다는 느림) - 주로 컨트롤러 테스트용

  @Autowired
  ObjectMapper objectMapper;

  // accept 헤더를 주는게 좋음
  @Test
  public void createEvent() throws Exception {
    // id(100) free(true) eventStatus... 를 넣더라도 Dto로 전달하면서 무시됨
    Event event = Event.builder()
            .id(100)
            .name("spring")
            .description("start spring!")
            .beginEnrollmentDateTime(LocalDateTime.of(2022, 3, 1, 12, 0))
            .closeEnrollmentDateTime(LocalDateTime.of(2022, 3, 7, 12, 0))
            .beginEventDateTime(LocalDateTime.of(2022, 3, 8, 12, 0))
            .endEventDateTime(LocalDateTime.of(2022, 3, 9, 12, 0))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남역")
            .free(true)
            .offline(true)
            .eventStatus(EventStatus.PUBLISHED)
            .build();

    mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("id").value(Matchers.not(100)))
            .andExpect(jsonPath("free").value(Matchers.not(true)))
            .andExpect(jsonPath("offline").value(Matchers.not(true)))
            .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
  }
}
