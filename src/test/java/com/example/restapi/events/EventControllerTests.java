package com.example.restapi.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
public class EventControllerTests {

  // 웹과 관련한 빈들만 등록 -> 단위 테스트라고 보기는 어려움 -dispatcher(핸들러, 매퍼, 컨버터) eventcontroller...
  @Autowired
  MockMvc mockMvc; // 디스패치 서블릿은 만들지만 웹서버를 띄우지 않아 빠름(단위T 보다는 느림) - 주로 컨트롤러 테스트용

  // accept 헤더를 주는게 좋음
  @Test
  public void createEvent() throws Exception {
    mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    )
            .andExpect(status().isCreated());
  }
}
