package org.kt.hw.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kt.hw.auth.Auth;
import org.kt.hw.entity.Account;
import org.kt.hw.entity.User;
import org.kt.hw.model.*;
import org.kt.hw.repository.AccountRepository;
import org.kt.hw.services.AccountService;
import org.kt.hw.services.exceptions.NotEnoughMoneyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @RequestMapping(value = "/accounts", method = RequestMethod.POST)
    public ResponseEntity<CreateAccountResponse> createAccount(@RequestBody CreateAccountRequest data) {
        if (data == null || data.getOwnerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner is empty");
        }

        Account newAccount = accountService.createAccount(data.getOwnerId());
        accountRepository.save(newAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAccountResponse(newAccount.getId()));

    }

    @RequestMapping(value = "/billing/balance", method = RequestMethod.GET)
    public ResponseEntity<AccountResponse> getBalance(HttpServletRequest request) {
        Account account = getAccount(request);

        return ResponseEntity.ok(new AccountResponse(account.getId(), account.getBalance()));
    }

    @RequestMapping(value = "/billing/deposit", method = RequestMethod.POST)
    public ResponseEntity<AccountResponse> deposit(@RequestBody DepositRequest data, HttpServletRequest request) {
        Account account = getAccount(request);

        if (data == null || data.getAmount() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount is empty");
        }

        accountService.deposit(account, data.getAmount());
        accountRepository.save(account);
        return ResponseEntity.ok(new AccountResponse(account.getId(), account.getBalance()));

    }

    @RequestMapping(value = "/billing/withdraw", method = RequestMethod.POST)
    public ResponseEntity<AccountResponse> withdraw(@RequestBody WithdrawRequest data, HttpServletRequest request) {
        Account account = getAccount(request);

        if (data == null || data.getAmount() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount is empty");
        }

        try {
            accountService.withdraw(account, data.getAmount());
        } catch(NotEnoughMoneyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        accountRepository.save(account);
        return ResponseEntity.ok(new AccountResponse(account.getId(), account.getBalance()));
    }

    @RequestMapping(value = "/billing/reset", method = RequestMethod.POST)
    public ResponseEntity<Void> clear(HttpServletRequest request) {
        Account account = getAccount(request);
        account.setBalance(0);
        accountRepository.save(account);
        return ResponseEntity.ok().build();
    }

    private Account getAccount(HttpServletRequest request) {
        UserDTO user = Auth.preHandle(request);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Account account = accountRepository.findAccountByOwnerId(user.getId());
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        return account;
    }




}

