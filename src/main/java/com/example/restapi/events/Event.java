package com.example.restapi.events;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id") // 상호 참조 주의
@Entity
public class Event {

  @Id @GeneratedValue
  private Integer id;
  private String name;
  private String description;
  private LocalDateTime beginEnrollmentDateTime;
  private LocalDateTime closeEnrollmentDateTime;
  private LocalDateTime beginEventDateTime;
  private LocalDateTime endEventDateTime;
  private String location; // (optional) 이게 없으면 온라인 모임
  private int basePrice; // (optional)
  private int maxPrice; // (optional)
  private int limitOfEnrollment;
  private boolean offline;
  @Enumerated(EnumType.STRING)
  private EventStatus EventStatus;
}
