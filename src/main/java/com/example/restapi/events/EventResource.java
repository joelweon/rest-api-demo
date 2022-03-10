package com.example.restapi.events;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


// ObjectMapper가 BeanSerializer를 통해 객체(EventResource)를 json으로 직렬화(컨버팅)
// https://docs.spring.io/spring-hateoas/docs/current/reference/html/#migrate-to-1.0.changes.representation-models
public class EventResource extends EntityModel<Event> {

  public EventResource(Event event) {
    super(event);
    add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
  }
}
