package com.example.restapi.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

  @Autowired
  AccountService accountService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Test
  public void findByUsername() {
    // Given
    String username = "admin2@test.com";
    String password = "1234";
    Account account = Account.builder()
            .email(username)
            .password(password)
            .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
            .build();

    this.accountService.saveAccount(account);

    // When
    UserDetailsService userDetailsService = accountService;
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // Then
    assertTrue(passwordEncoder.matches(password, userDetails.getPassword()));
  }

  @Test
  public void findByUsernameFail() {

    String username = "random@test.com";
    try {
      this.accountService.loadUserByUsername(username);
      fail("supposed to be failed");
    } catch (UsernameNotFoundException e) {
      assertTrue(e.getMessage().contains(username));
    }
  }
}
