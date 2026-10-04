package com.minibank.account;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class AccountController {

    public record OpenAccountRequest(@NotNull Long userId) {}
    public record DepositRequest(@NotNull @Positive BigDecimal amount) {}
    public record TransferRequest(@NotNull Long fromId, @NotNull Long toId, @NotNull @Positive BigDecimal amount) {}

    private final AccountService service;
    private final ReceiptStorage receiptStorage;

    public AccountController(AccountService service, ReceiptStorage receiptStorage) {
        this.service = service;
        this.receiptStorage = receiptStorage;
    }

    @PostMapping("/api/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public Account open(@Valid @RequestBody OpenAccountRequest request) {
        return service.open(request.userId());
    }

    @PostMapping("/api/accounts/{id}/deposit")
    public Account deposit(@PathVariable Long id, @Valid @RequestBody DepositRequest request) {
        return service.deposit(id, request.amount());
    }

    @PostMapping("/api/transfers")
    public Receipt transfer(@Valid @RequestBody TransferRequest request) {
        Receipt receipt = service.transfer(request.fromId(), request.toId(), request.amount());
        // Upload only AFTER transfer() returned, i.e. after the database transaction committed
        receiptStorage.save(receipt);
        return receipt;
    }

    @GetMapping("/api/accounts/{id}")
    public Account get(@PathVariable Long id) {
        return service.get(id);
    }
}
