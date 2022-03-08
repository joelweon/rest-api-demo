package com.example.restapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

  // basePrice	maxPrice
  // 0	        100	     선착순 등록
  // 0	        0	       무료
  // 100	      0	       무제한 경매 (높은 금액 낸 사람이 등록)
  // 100	      200	     제한가 선착순 등록
  // 처음 부터 200을 낸 사람은 선 등록.
  // 100을 내고 등록할 수 있으나 더 많이 낸 사람에 의해 밀려날 수 있음.
  public void validate(EventDto eventDto, Errors errors) {
    if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
      errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong.");
      errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong.");
    }

    LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
    if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
            endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
            endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
      errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
    }

    // TODO valid beginEventDateTime
    // TODO valid closeEnrollmentDateTime
  }
}
