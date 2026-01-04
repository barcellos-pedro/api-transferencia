package com.itau.transferencia.services;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.entities.TransferStatus;
import com.itau.transferencia.http.requests.TransferRequest;
import com.itau.transferencia.http.responses.TransferResponse;
import com.itau.transferencia.repositories.TransferRepository;
import com.itau.transferencia.services.impl.TransferServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @InjectMocks
    private TransferServiceImpl service;

    @Mock
    private TransferRepository repository;

    @Mock
    private CustomerService customerService;

    @Mock
    private TransferLogService transferLogService;

    @Test
    void transfer() {
        var sourceAccount = "00005-5";
        var destinationAccount = "00006-6";

        var sourceCustomer = new Customer("Bob", sourceAccount, BigDecimal.valueOf(100));
        var destinationCustomer = new Customer("Ross", destinationAccount);

        var amount = BigDecimal.valueOf(50);
        var transferRequest = new TransferRequest(destinationAccount, amount);
        var transfer = Transfer.ofCompleted(sourceCustomer, destinationCustomer, amount);

        when(customerService.findByAccount(sourceAccount)).thenReturn(Optional.of(sourceCustomer));
        when(customerService.findByAccount(destinationAccount)).thenReturn(Optional.of(destinationCustomer));
        when(repository.save(any())).thenReturn(transfer);

        var transferResult = service.transfer(sourceAccount, transferRequest);

        assertThat(sourceCustomer.getBalance()).isEqualByComparingTo("50");
        assertThat(destinationCustomer.getBalance()).isEqualByComparingTo("50");

        assertThat(transferResult.getAmount()).isEqualByComparingTo(amount);
        assertThat(transferResult.getStatus()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(transferResult.getSource().getAccount()).isEqualTo(sourceAccount);
        assertThat(transferResult.getDestination().getAccount()).isEqualTo(destinationAccount);

    }

    @Test
    void transferFailsWhenSameAccount() {

    }

    @Test
    void transferFailsWhenAccountNotFound() {

    }

    @Test
    void transferFailsWhenUnsufficientFunds() {

    }

    @Test
    void getTransfers() {
        var source = new Customer("Bob", "00005-5", BigDecimal.valueOf(100));
        var destination = new Customer("Ross", "00006-6");

        var completedTransfer = Transfer.ofCompleted(source, destination, BigDecimal.valueOf(50));
        var failedTransfer = Transfer.ofFailed(source, destination, BigDecimal.valueOf(70));

        var account = "00001-1";

        when(repository.getTransfers(account)).thenReturn(List.of(completedTransfer, failedTransfer));

        List<TransferResponse> result = service.getTransfers(account);

        assertThat(result.size()).isNotZero();
        assertThat(result.getFirst().status()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(result.getFirst().sourceAccount()).isEqualTo("00005-5");
        assertThat(result.getFirst().destinationAccount()).isEqualTo("00006-6");
        assertThat(result.getFirst().amount()).isEqualByComparingTo("50");
        assertThat(result.getLast().status()).isEqualTo(TransferStatus.FAILED);

        verify(repository).getTransfers(account);
    }
}