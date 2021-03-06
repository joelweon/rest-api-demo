package com.example.restapi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

@JsonComponent // ObjectMapper에 등록하기
public class ErrorsSerializer extends JsonSerializer<Errors> { // ObjectMapper가 Errors 객체를 serializer 할때 ErrorsSerializer를 사용함

  @Override
  public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    // 스프링 부트 2.3으로 올라가면서 Jackson 라이브러리가 더이상 Array부터 만드는걸 허용하지 않음
    jsonGenerator.writeFieldName("errors");
    jsonGenerator.writeStartArray();

    errors.getFieldErrors().forEach(e -> {
      try {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("field", e.getField());
        jsonGenerator.writeStringField("objectName", e.getObjectName());
        jsonGenerator.writeStringField("code", e.getCode());
        jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
        Object rejectedValue = e.getRejectedValue();
        if (rejectedValue != null) {
          jsonGenerator.writeStringField("rejectedValue", String.valueOf(rejectedValue));
        }
        jsonGenerator.writeEndObject();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    });

    errors.getGlobalErrors().forEach(e -> {
      try {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("objectName", e.getObjectName());
        jsonGenerator.writeStringField("code", e.getCode());
        jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
        jsonGenerator.writeEndObject();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    });

    jsonGenerator.writeEndArray();
  }
}
