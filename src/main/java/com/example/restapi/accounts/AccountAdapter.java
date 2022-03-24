package com.example.restapi.accounts;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountAdapter extends User {

  private Account account;

  public AccountAdapter(Account account) {
    super(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
    this.account = account;
  }

  // AuthenticationPrincipal에서 account 가져감
  public Account getAccount() {
    return account;
  }

  private static Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
    return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE " + r.name()))
            .collect(Collectors.toSet());
  }
}
