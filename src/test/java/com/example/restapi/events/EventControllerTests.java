package com.example.restapi.events;

import com.example.restapi.common.RestDocsConfiguration;
import com.example.restapi.common.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
// @WebMvcTest // 슬라이스용 테스트라 Web용 빈들만 등록 해줌(repository는 등록해주지 않음) -> @SpringBootTest로 변경
@SpringBootTest // @SpringBootApplication 하위의 Bean들을 찾아서 등록해줌(Web app과 가장 유사한 형태가 됨)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {

  // 웹과 관련한 빈들만 등록 -> 단위 테스트라고 보기는 어려움 -dispatcher(핸들러, 매퍼, 컨버터) eventcontroller...
  @Autowired
  MockMvc mockMvc; // 디스패치 서블릿은 만들지만 웹서버를 띄우지 않아 빠름(단위T 보다는 느림) - 주로 컨트롤러 테스트용

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  EventRepository eventRepository;

  private final String EVENT_URL = "/api/events";

  @Test
  @DisplayName("정상적으로 이벤트를 생성하는 테스트")
  public void createEvent() throws Exception {
    EventDto event = EventDto.builder()
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
            .build();

    mockMvc.perform(post(EVENT_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
            .andExpect(jsonPath("free").value(false))
            .andExpect(jsonPath("offline").value(true))
            .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
            // 스니펫 추가
            .andDo(document("create-event",
                    links(
                            linkWithRel("self").description("link to self"),
                            linkWithRel("query-event").description("link to query-event"),
                            linkWithRel("update-event").description("link to update-event"),
                            linkWithRel("profile").description("link to profile")
                    ),
                    requestHeaders(
                            headerWithName(HttpHeaders.ACCEPT).description("ACCEPT header"),
                            headerWithName(HttpHeaders.CONTENT_TYPE).description("CONTENT_TYPE header")
                    ),
                    requestFields(
                            fieldWithPath("name").description("Name of new event"),
                            fieldWithPath("description").description("description of new event"),
                            fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                            fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                            fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                            fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                            fieldWithPath("location").description("location of new event"),
                            fieldWithPath("basePrice").description("basePrice of new event"),
                            fieldWithPath("maxPrice").description("maxPrice of new event"),
                            fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
                    ),
                    responseHeaders(
                            headerWithName("Location").description("Location header - 새로 생성된 이벤트를 조회하는 URL"),
                            headerWithName("Content-Type").description("Content-Type header - application/hal+json")
                    ),
                    responseFields(
                            fieldWithPath("id").description("identifier of new event"),
                            fieldWithPath("name").description("Name of new event"),
                            fieldWithPath("description").description("description of new event"),
                            fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                            fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                            fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                            fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                            fieldWithPath("location").description("location of new event"),
                            fieldWithPath("basePrice").description("basePrice of new event"),
                            fieldWithPath("maxPrice").description("maxPrice of new event"),
                            fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                            fieldWithPath("offline").description("It tells if this event is offline meeting or not"),
                            fieldWithPath("free").description("It tells if this event is free or not"),
                            fieldWithPath("eventStatus").description("eventStatus"),

                            fieldWithPath("_links.self.href").description("link to self"),
                            fieldWithPath("_links.query-event.href").description("link to query-event"),
                            fieldWithPath("_links.update-event.href").description("link to update-event"),
                            fieldWithPath("_links.profile.href").description("link to profile")
                    )
            ));
  }
  // HATEOAS
  // - 응답으로 F/E에서 처리하기 쉽도록 Links를 제공하는 것.
  // - link 정보가 없으면 현재 상태에서 어떠한 애플리케이션 상태로 전이를 하지 못함

  @Test
  @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
  @TestDescription("strict한 방법")
  public void createEvent_Bad_Request() throws Exception {
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
            .eventStatus(EventStatus.PUBLISHED)
            .build();

    mockMvc.perform(post(EVENT_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
  public void createEvent_Bad_Request_Empty_Input() throws Exception {
    EventDto eventDto = EventDto.builder().build();

    this.mockMvc.perform(post(EVENT_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
  public void createEvent_Bad_Request_Wrong_Input() throws Exception {
    EventDto eventDto = EventDto.builder()
            .name("spring")
            .description("start spring!")
            .beginEnrollmentDateTime(LocalDateTime.of(2022, 3, 20, 12, 0))
            .closeEnrollmentDateTime(LocalDateTime.of(2022, 3, 10, 12, 0))
            .beginEventDateTime(LocalDateTime.of(2022, 3, 20, 12, 0))
            .endEventDateTime(LocalDateTime.of(2022, 3, 10, 12, 0))
            .basePrice(10000)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .build();

    this.mockMvc.perform(post(EVENT_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errors[0].objectName").exists())
            .andExpect(jsonPath("errors[0].defaultMessage").exists())
            .andExpect(jsonPath("errors[0].code").exists())
            .andExpect(jsonPath("_links.index").exists())
            .andDo(print());
  }

  @Test
  @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
  public void queryEvents() throws Exception {
    // Given
    IntStream.range(0, 30).forEach(this::generateEvent);

    // When & Then
    this.mockMvc.perform(get(EVENT_URL)
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "name,DESC"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("page").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("query-events"));
  }


  @Test
  @DisplayName("기존 이벤트 하나 조회하기")
  public void getEvent() throws Exception {
    // Given
    Event event = this.generateEvent(100);

    // When & Then
    this.mockMvc.perform(get(EVENT_URL + "/{id}", event.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("name").exists())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("get-an-event"));
  }

  @Test
  @DisplayName("없는 이벤트는 조회했을 때 404 응답받기")
  public void getEvent404() throws Exception {
    // When & Then
    this.mockMvc.perform(get(EVENT_URL + "/99999"))
            .andExpect(status().isNotFound());
  }

  private Event generateEvent(int index) {
    Event event = Event.builder()
            .name("event" + index)
            .description("test event")
            .build();

    return this.eventRepository.save(event);
  }
}
