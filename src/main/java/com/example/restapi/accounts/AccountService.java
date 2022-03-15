package com.example.restapi.accounts;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {

  final AccountRepository accountRepository;

  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override // spring security가 정의한 인터페이스로 변환함
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = accountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
  }

  private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
    return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE " + r.name()))
            .collect(Collectors.toSet());
  }
}
