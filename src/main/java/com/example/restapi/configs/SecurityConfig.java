package com.example.restapi.configs;

import com.example.restapi.accounts.AccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  final AccountService accountService;

  final PasswordEncoder passwordEncoder;

  public SecurityConfig(AccountService accountService, PasswordEncoder passwordEncoder) {
    this.accountService = accountService;
    this.passwordEncoder = passwordEncoder;
  }

  // @Bean // 기본값
  // public TokenStore tokenStore() {
  //   return new InMemoryTokenStore();
  // }

  @Bean
  @Override // 다른 authorization 서버나 리소스 서버에서 참조 가능하게 Bean 등록
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override // Authentication 만드는 방법 정의
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(accountService).passwordEncoder(passwordEncoder);
  }

  @Override // 스프링 시큐리티 필터를 적용할지 여부 결정
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()
            .mvcMatchers("/docs/index.html")
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.anonymous()
            .and().formLogin()
            .and().authorizeRequests()
            .mvcMatchers(HttpMethod.GET, "/api/**").authenticated()
            .anyRequest().authenticated();

  }
}
