package com.example.restapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

  private final EventRepository eventRepository;
  private final ModelMapper modelMapper;
  private final EventValidator eventValidator;

  public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
    this.eventRepository = eventRepository;
    this.modelMapper = modelMapper;
    this.eventValidator = eventValidator;
  }

  @PostMapping
  public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(errors);
    }

    eventValidator.validate(eventDto, errors);
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(errors);
    }

    Event event = modelMapper.map(eventDto, Event.class);
    event.update();
    Event newEvent = this.eventRepository.save(event);

    // linkTo(): 컨트롤러나 핸들러 메소드로부터 URI 정보 읽어올 때 쓰는 메소드
    // location 헤더에 넣어줄 URI
    WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
    URI createUri = selfLinkBuilder.toUri();
    EventResource eventResource = new EventResource(event);
    eventResource.add(linkTo(EventController.class).withRel("query-event"));
    eventResource.add(selfLinkBuilder.withRel("update-event"));
    return ResponseEntity.created(createUri).body(eventResource);
  }
}
