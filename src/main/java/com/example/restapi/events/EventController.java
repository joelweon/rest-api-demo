package com.example.restapi.events;

import com.example.restapi.common.ErrorResource;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
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
      return badRequest(errors);
    }

    eventValidator.validate(eventDto, errors);
    if (errors.hasErrors()) {
      return badRequest(errors);
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
    eventResource.add(Link.of("/docs/index.html#resources-events-create", LinkRelation.of("profile")));
    return ResponseEntity.created(createUri).body(eventResource);
  }

  // badRequest인 경우 에러를 받아서 Resource로 변환(index링크 추가)하여 본문에 담아줌
  private ResponseEntity<ErrorResource> badRequest(Errors errors) {
    return ResponseEntity.badRequest().body(new ErrorResource(errors));
  }
}
