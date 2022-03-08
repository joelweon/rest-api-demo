package com.example.restapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

  private final EventRepository eventRepository;
  private final ModelMapper modelMapper;

  // 생성자가 하나만 있고 파라미터가 빈으로 등록되어 있다면 @Autowired 생략 가능(spring4.3 ~)
  public EventController(EventRepository eventRepository, ModelMapper modelMapper) {
    this.eventRepository = eventRepository;
    this.modelMapper = modelMapper;
  }

  @PostMapping
  public ResponseEntity createEvent(@RequestBody EventDto eventDto) {
    Event event = modelMapper.map(eventDto, Event.class);
    Event newEvent = this.eventRepository.save(event);

    URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
    return ResponseEntity.created(createUri).body(event);
  }
}
