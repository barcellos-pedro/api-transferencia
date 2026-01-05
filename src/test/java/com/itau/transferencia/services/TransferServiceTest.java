package com.itau.transferencia.services;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.entities.Transfer;
import com.itau.transferencia.entities.TransferStatus;
import com.itau.transferencia.exceptions.BusinessException;
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

import static com.itau.transferencia.helpers.ErrorMessages.ACCOUNT_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
        var account = "00005-5";
        var customer = new Customer("Bob", account, BigDecimal.valueOf(100));
        var transferRequest = new TransferRequest(account, BigDecimal.valueOf(50));
        var failedTransfer = Transfer.ofFailed(customer, customer, transferRequest.amount());

        when(customerService.findByAccount(account)).thenReturn(Optional.of(customer));

        var exception = assertThrows(BusinessException.class, () -> service.transfer(account, transferRequest));
        assertThat(exception.getMessage()).isEqualTo("Cannot transfer to the same account.");
        assertThat(exception.getHttpStatus()).isEqualTo(BAD_REQUEST);
        verify(transferLogService).save(failedTransfer);
    }

    @Test
    void transferFailsWhenAccountNotFound() {
        var account = "00005-5";
        var errorMessage = ACCOUNT_NOT_FOUND.formatted(account);
        var transferRequest = new TransferRequest(account, BigDecimal.valueOf(50));
        var failedTransfer = Transfer.ofFailed(null, null, transferRequest.amount());

        when(customerService.findByAccount(account)).thenReturn(Optional.empty());

        var exception = assertThrows(BusinessException.class, () -> service.transfer(account, transferRequest));

        assertThat(exception.getMessage()).isEqualTo(errorMessage);
        assertThat(exception.getHttpStatus()).isEqualTo(NOT_FOUND);
        verify(transferLogService).save(failedTransfer);
    }

    @Test
    void transferFailsWhenUnsufficientFunds() {
        var sourceAccount = "00005-5";
        var destinationAccount = "00006-6";

        var sourceCustomer = new Customer("Bob", sourceAccount);
        var destinationCustomer = new Customer("Ross", destinationAccount);

        var amount = BigDecimal.valueOf(50);
        var transferRequest = new TransferRequest(destinationAccount, amount);
        var failedTransfer = Transfer.ofFailed(sourceCustomer, destinationCustomer, transferRequest.amount());

        when(customerService.findByAccount(sourceAccount)).thenReturn(Optional.of(sourceCustomer));
        when(customerService.findByAccount(destinationAccount)).thenReturn(Optional.of(destinationCustomer));

        var exception = assertThrows(BusinessException.class, () -> service.transfer(sourceAccount, transferRequest));

        assertThat(exception.getMessage()).isEqualTo("Insufficient funds for this operation.");
        assertThat(exception.getHttpStatus()).isEqualTo(BAD_REQUEST);
        verify(transferLogService).save(failedTransfer);
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