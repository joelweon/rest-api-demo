package com.example.restapi.events;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


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

  @Test
  public void testFree() {
    // Given
    Event event = Event.builder()
            .basePrice(0)
            .maxPrice(0)
            .build();

    // When
    event.update();

    // Then
    assertTrue(event.isFree());

    // Given
    event = Event.builder()
            .basePrice(100)
            .maxPrice(0)
            .build();

    // When
    event.update();

    // Then
    assertFalse(event.isFree());

    // Given
    event = Event.builder()
            .basePrice(0)
            .maxPrice(100)
            .build();

    // When
    event.update();

    // Then
    assertFalse(event.isFree());
  }

  @Test
  public void testOffline() {
    // Given
    Event event = Event.builder()
            .location("강남역")
            .build();

    // When
    event.update();

    // Then
    assertTrue(event.isOffline());

    // Given
    event = Event.builder()
            .build();

    // When
    event.update();

    // Then
    assertFalse(event.isOffline());
  }

}
