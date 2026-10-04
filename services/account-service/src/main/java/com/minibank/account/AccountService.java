package com.minibank.account;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository repository;
    private final UserClient userClient;

    public AccountService(AccountRepository repository, UserClient userClient) {
        this.repository = repository;
        this.userClient = userClient;
    }

    public Account open(Long userId) {
        if (!userClient.userExists(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User " + userId + " does not exist");
        }
        return repository.save(new Account(userId));
    }

    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        Account account = get(accountId);
        account.deposit(amount);
        return account; // no save() needed: inside a transaction JPA writes changes automatically
    }

    // All-or-nothing: if anything fails half way, the database rolls BOTH accounts back
    @Transactional
    public Receipt transfer(Long fromId, Long toId, BigDecimal amount) {
        if (fromId.equals(toId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot transfer to the same account");
        }
        Account from = get(fromId);
        Account to = get(toId);
        if (from.getBalance().compareTo(amount) < 0) { // compareTo, not <, for BigDecimal
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Insufficient balance in account " + fromId);
        }
        from.withdraw(amount);
        to.deposit(amount);
        return new Receipt(UUID.randomUUID().toString(), fromId, toId, amount, Instant.now());
    }

    public Account get(Long accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account " + accountId + " not found"));
    }
}
