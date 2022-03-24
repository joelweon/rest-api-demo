package com.example.restapi.events;

import com.example.restapi.accounts.Account;
import com.example.restapi.accounts.AccountRepository;
import com.example.restapi.accounts.AccountRole;
import com.example.restapi.accounts.AccountService;
import com.example.restapi.common.BaseControllerTest;
import com.example.restapi.common.TestDescription;
import com.example.restapi.configs.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EventControllerTests extends BaseControllerTest {

  @Autowired
  EventRepository eventRepository;
  @Autowired
  AccountService accountService;
  @Autowired
  AccountRepository accountRepository;
  @Autowired
  AppProperties appProperties;

  @BeforeEach
  public void setUp() {
    this.eventRepository.deleteAll();
    this.accountRepository.deleteAll();
  }

  private final String EVENT_URL = "/api/events";


  @Test
  @DisplayName("POST: 정상적으로 이벤트를 생성하는 테스트")
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                            fieldWithPath("manager.id").description("id of manager"),

                            fieldWithPath("_links.self.href").description("link to self"),
                            fieldWithPath("_links.query-event.href").description("link to query-event"),
                            fieldWithPath("_links.update-event.href").description("link to update-event"),
                            fieldWithPath("_links.profile.href").description("link to profile")
                    )
            ));
    // HATEOAS
    // - 응답으로 F/E에서 처리하기 쉽도록 Links를 제공하는 것.
    // - link 정보가 없으면 현재 상태에서 어떠한 애플리케이션 상태로 전이를 하지 못함
  }

  private String getBearerToken() throws Exception {
    return getBearerToken(true);
  }

  private String getBearerToken(Boolean needToCreateAccount) throws Exception {
    return "Bearer " + getAccessToken(needToCreateAccount);
  }

  private String getAccessToken(Boolean needToCreateAccount) throws Exception {
    // Given
    if (needToCreateAccount) {
      createAccount();
    }

    // When & Then
    ResultActions perform = this.mockMvc.perform(post("/oauth/token")
            .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
            .param("username", appProperties.getUserUsername())
            .param("password", appProperties.getUserPassword())
            .param("grant_type", "password"));
    var response = perform.andReturn().getResponse();
    var responseBody = response.getContentAsString();
    JacksonJsonParser jsonParser = new JacksonJsonParser();
    return jsonParser.parseMap(responseBody).get("access_token").toString();
  }

  private Account createAccount() {
    Account admin = Account.builder()
            .email(appProperties.getUserUsername())
            .password(appProperties.getUserPassword())
            .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
            .build();
    return this.accountService.saveAccount(admin);
  }


  @Test
  @DisplayName("POST: 입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
            .andDo(print())
            .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("POST: 입력 값이 비어있는 경우에 에러가 발생하는 테스트")
  public void createEvent_Bad_Request_Empty_Input() throws Exception {
    EventDto eventDto = EventDto.builder().build();

    this.mockMvc.perform(post(EVENT_URL)
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  @DisplayName("POST: 입력 값이 잘못된 경우에 에러가 발생하는 테스트")
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
  @DisplayName("GET: 30개의 이벤트를 10개씩 두번째 페이지 조회하기")
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
  @DisplayName("GET: 30개의 이벤트를 10개씩 두번째 페이지 조회하기")
  public void queryEventsWithAuthentication() throws Exception {
    // Given
    IntStream.range(0, 30).forEach(this::generateEvent);

    // When & Then
    this.mockMvc.perform(get(EVENT_URL)
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "name,DESC"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("page").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andExpect(jsonPath("_links.create-event").exists())
            .andDo(document("query-events"));
  }

  @Test
  @DisplayName("GET: 기존 이벤트 하나 조회하기")
  public void getEvent() throws Exception {
    // Given
    Account account = this.createAccount();
    Event event = this.generateEvent(100, account);

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
  @DisplayName("GET: 없는 이벤트는 조회했을 때 404 응답받기")
  public void getEvent404() throws Exception {
    // When & Then
    this.mockMvc.perform(get(EVENT_URL + "/99999"))
            .andExpect(status().isNotFound());
  }


  @Test
  @DisplayName("PUT: 이벤트를 정상적으로 수정하기")
  public void updateEvent() throws Exception {
    // Given
    Account account = this.createAccount();
    Event event = this.generateEvent(100, account);

    String eventName = "Updated Name";
    EventDto eventDto = this.modelMapper.map(event, EventDto.class);
    eventDto.setName(eventName);


    // When & Then
    this.mockMvc.perform(put(EVENT_URL + "/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
            .andExpect(jsonPath("name").value(eventName))
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(status().isOk())
            .andDo(print());
  }

  @Test
  @DisplayName("PUT: 입력값이 잘못된 경우에 이벤트 수정 실패 1 - 없는 값")
  public void updateEvent400_Empty() throws Exception {
    // Given
    Event event = this.generateEvent(100);
    EventDto eventDto = new EventDto();

    this.mockMvc.perform(put(EVENT_URL + "/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  @DisplayName("PUT: 입력값이 잘못된 경우에 이벤트 수정 실패 2 - 로직상 잘못")
  public void updateEvent400_Wrong() throws Exception {
    // Given
    Event event = this.generateEvent(100);
    event.setBasePrice(100000);
    event.setMaxPrice(10);

    this.mockMvc.perform(put(EVENT_URL + "/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
            .andExpect(status().isBadRequest())
            .andDo(print());
  }


  @Test
  @DisplayName("PUT: 없는 이벤트 수정하는 경우 404 NOT_FOUND")
  public void updateEvent404_Empty() throws Exception {
    // Given
    Event event = this.generateEvent(100);
    EventDto eventDto = this.modelMapper.map(event, EventDto.class);

    // When & Then
    this.mockMvc.perform(put(EVENT_URL + "/9999")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("PUT: 존재하지 않는 이벤트 수정 실패")
  public void updateEvent404() throws Exception {
    // Given
    Event event = this.generateEvent(100);
    event.setBasePrice(100000);
    event.setMaxPrice(10);

    this.mockMvc.perform(put(EVENT_URL + "/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
            .andExpect(status().isBadRequest())
            .andDo(print());
  }

  private Event generateEvent(int index, Account account) {
    Event event = buildEvent(index);
    event.setManager(account);
    return this.eventRepository.save(event);
  }

  private Event generateEvent(int index) {
    Event event = buildEvent(index);
    return this.eventRepository.save(event);
  }

  private Event buildEvent(int index) {
    return Event.builder()
            .name("event" + index)
            .description("test event")
            .beginEnrollmentDateTime(LocalDateTime.of(2022, 3, 1, 12, 0))
            .closeEnrollmentDateTime(LocalDateTime.of(2022, 3, 7, 12, 0))
            .beginEventDateTime(LocalDateTime.of(2022, 3, 8, 12, 0))
            .endEventDateTime(LocalDateTime.of(2022, 3, 9, 12, 0))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남역")
            .free(false)
            .offline(true)
            .eventStatus(EventStatus.DRAFT)
            .build();
  }
}
