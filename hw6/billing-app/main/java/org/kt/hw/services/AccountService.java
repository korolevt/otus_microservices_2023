package org.kt.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.entity.Account;
import org.kt.hw.services.exceptions.NotEnoughMoneyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
//@Scope("singleton")
public class AccountService {
    // Создать нового пользователя
    public Account createAccount(Integer ownerId) {
        return new Account(
                null,
                ownerId,
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
}
