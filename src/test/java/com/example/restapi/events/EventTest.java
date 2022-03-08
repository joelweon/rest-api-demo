package com.example.restapi.events;

import org.junit.jupiter.api.Test;

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
}
