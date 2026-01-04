package com.itau.transferencia.services;

import com.itau.transferencia.entities.Customer;
import com.itau.transferencia.http.requests.CustomerRequest;
import com.itau.transferencia.repositories.CustomerRepository;
import com.itau.transferencia.services.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerServiceImpl service;

    @Mock
    private CustomerRepository repository;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    @Test
    void create() {
        var customerRequest = new CustomerRequest("Jhon", "00000-1", BigDecimal.valueOf(100));
        service.create(customerRequest);

        verify(repository).save(customerCaptor.capture());
        var newCustomer = customerCaptor.getValue();

        assertThat(newCustomer.getName()).isEqualTo("Jhon");
        assertThat(newCustomer.getAccount()).isEqualTo("00000-1");
        assertThat(newCustomer.getBalance()).isEqualByComparingTo("100");
    }

    @Test
    void findAll() {
        var customer = new Customer("Boss", "00009-9");
        when(repository.findAll()).thenReturn(List.of(customer));

        var customers = service.findAll();

        assertThat(customers.size()).isNotZero();
        assertThat(customers.getFirst().getName()).isEqualTo("Boss");
        verify(repository).findAll();
    }

    @Test
    void findByAccount() {
        var account = "00009-9";
        var customer = new Customer("Harry", account);
        when(repository.findByAccount(anyString())).thenReturn(Optional.of(customer));

        var result = service.findByAccount(account);

        assertThat(result).isPresent()
                .get()
                .isEqualTo(customer);

        verify(repository).findByAccount(account);
    }

    @Test
    void findByAccountNotFound() {
        var account = "00009-9";
        when(repository.findByAccount(anyString())).thenReturn(Optional.empty());

        var result = service.findByAccount(account);

        assertThat(result).isEmpty();
        verify(repository).findByAccount(account);
    }
}