package com.example.restapi.events;

import com.example.restapi.accounts.Account;
import com.example.restapi.accounts.AccountSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
  private boolean free;
  @Enumerated(EnumType.STRING)
  private EventStatus eventStatus = EventStatus.DRAFT;
  @ManyToOne
  @JsonSerialize(using = AccountSerializer.class)
  private Account manager;

  public void update() {
    this.setFree(this.basePrice == 0 && this.maxPrice == 0);
    this.setOffline(this.getLocation() != null && !this.getLocation().isBlank());
  }
}
