package com.minibank.account;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Plain unit test: no Spring, no database. Mocks stand in for the repository and user-service.
class AccountServiceTest {

    private final AccountRepository repository = mock(AccountRepository.class);
    private final AccountService service = new AccountService(repository, mock(UserClient.class));

    private Account accountWith(String balance) {
        Account account = new Account(1L);
        account.deposit(new BigDecimal(balance));
        return account;
    }

    @Test
    void transferMovesMoneyBetweenAccounts() {
        Account from = accountWith("100.00");
        Account to = accountWith("20.00");
        when(repository.findById(1L)).thenReturn(Optional.of(from));
        when(repository.findById(2L)).thenReturn(Optional.of(to));

        service.transfer(1L, 2L, new BigDecimal("30.00"));

        assertThat(from.getBalance()).isEqualByComparingTo("70.00");
        assertThat(to.getBalance()).isEqualByComparingTo("50.00");
    }

    @Test
    void transferRejectsInsufficientBalance() {
        Account from = accountWith("10.00");
        Account to = accountWith("0.00");
        when(repository.findById(1L)).thenReturn(Optional.of(from));
        when(repository.findById(2L)).thenReturn(Optional.of(to));

        assertThatThrownBy(() -> service.transfer(1L, 2L, new BigDecimal("50.00")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Insufficient balance");

        // Nothing moved
        assertThat(from.getBalance()).isEqualByComparingTo("10.00");
        assertThat(to.getBalance()).isEqualByComparingTo("0.00");
    }
}
