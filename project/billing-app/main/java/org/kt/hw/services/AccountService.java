package org.kt.hw.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.auth.Auth;
import org.kt.hw.entity.Account;
import org.kt.hw.model.User;
import org.kt.hw.repository.AccountRepository;
import org.kt.hw.services.exceptions.NotEnoughMoneyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
//@Scope("singleton")
public class AccountService {
    private final AccountRepository accountRepository;

    // Создать нового пользователя
    public Account createAccount(Integer userId) {
        return new Account(
                null,
                userId,
                0
        );
    }

    public void deposit(Account account, int amount) {
        account.setBalance(account.getBalance() + amount);
    }

    public void withdraw(Account account, int amount) {
        if (account.getBalance() < amount) {
            throw new NotEnoughMoneyException();
        }

        account.setBalance(account.getBalance() - amount);
    }

    public Account getAccount(HttpServletRequest request) {
        User user = Auth.preHandle(request);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Account account = accountRepository.findAccountByUserId(user.getId());
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        return account;
    }
}
