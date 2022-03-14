package com.example.restapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
// @WebMvcTest // 슬라이스용 테스트라 Web용 빈들만 등록 해줌(repository는 등록해주지 않음) -> @SpringBootTest로 변경
@SpringBootTest // @SpringBootApplication 하위의 Bean들을 찾아서 등록해줌(Web app과 가장 유사한 형태가 됨)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Disabled
public abstract class BaseControllerTest {

  // 웹과 관련한 빈들만 등록 -> 단위 테스트라고 보기는 어려움 -dispatcher(핸들러, 매퍼, 컨버터) eventcontroller...
  @Autowired
  protected MockMvc mockMvc; // 디스패치 서블릿은 만들지만 웹서버를 띄우지 않아 빠름(단위T 보다는 느림) - 주로 컨트롤러 테스트용

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected ModelMapper modelMapper;
}
