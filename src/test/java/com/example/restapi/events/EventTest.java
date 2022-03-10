package com.example.restapi.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class EventTest {

  @Test
  public void builder() {
    Event event = Event.builder()
            .name("Event1")
            .description("spring")
            .build();
    assertNotNull(event);
  }

  @Test
  public void javaBean() {
    // Given
    Event event = new Event();
    String name = "Event1";
    String description = "spring";

    // When
    event.setName(name);
    event.setDescription(description);

    // Then
    assertEquals(event.getName(), name);
    assertEquals(event.getDescription(), description);
  }

  @ParameterizedTest
  @MethodSource
  public void testFree(int basePrice, int maxPrice, boolean isFree) {
    // Given
    Event event = Event.builder()
            .basePrice(basePrice)
            .maxPrice(maxPrice)
            .build();

    // When
    event.update();

    // Then
    assertEquals(event.isFree(), isFree);
  }

  // JUnit Jupiter는 convention에 의해 현재 @ParameterizedTest의 이름과 같은 factory method를 찾는다.
  // 다르게 주고 싶다면 @MethodSource("이름") 선언
  private static Stream<Arguments> testFree() {
    return Stream.of(
            Arguments.arguments(0, 0, true),
            Arguments.arguments(100, 0, false),
            Arguments.arguments(0, 100, false));
    // private static Object[] parametersForTestFree() {
    //   return new Object[] {
    //           new Object[] { 0, 0, true},
    //           new Object[] { 100, 0, false},
    //           new Object[] { 0, 100, false},
    //   };
    // }
  }

  @ParameterizedTest
  @CsvSource({"강남역, true", ", false"})
  public void testOffline(String location, boolean isOffline) {
    // Given
    Event event = Event.builder()
            .location(location)
            .build();

    // When
    event.update();

    // Then
    assertEquals(event.isOffline(), isOffline);
  }

}
