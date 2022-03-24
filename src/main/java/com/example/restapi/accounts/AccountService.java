package com.example.restapi.accounts;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

  final AccountRepository accountRepository;

  final PasswordEncoder passwordEncoder;

  public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
    this.accountRepository = accountRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Account saveAccount(Account account) {
    account.setPassword(this.passwordEncoder.encode(account.getPassword()));
    return accountRepository.save(account);
  }

  @Override // spring security가 정의한 인터페이스로 변환함
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = accountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    return new AccountAdapter(account);
  }
}
