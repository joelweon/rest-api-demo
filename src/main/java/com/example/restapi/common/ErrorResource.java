package com.example.restapi.common;

import com.example.restapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorResource extends EntityModel<Errors> {
  public ErrorResource(Errors content) {
    super(content);
    add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
  }
}
