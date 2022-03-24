package com.example.restapi.accounts;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account") // User 객체 안의 account를 바로 받음
// @AuthenticationPrincipal User user -> getPrincipal()로 받는 객체를 바로 받을 수 있음
public @interface CurrentUser {
}
