package com.itau.transferencia.controllers;

import com.itau.transferencia.enums.TransferStatus;
import com.itau.transferencia.repositories.CustomerRepository;
import com.itau.transferencia.repositories.TransferRepository;
import com.itau.transferencia.dtos.CustomerDTO;
import com.itau.transferencia.dtos.TransferDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static com.itau.transferencia.helpers.ErrorMessages.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Test
    void getAll() throws Exception {
        mockMvc.perform(get("/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Pedro"))
                .andExpect(jsonPath("$[0].account").value("00001-1"))
                .andExpect(jsonPath("$[0].balance").value(100.00))
                .andExpect(jsonPath("$[0].sentTransfers.length()").value(2))
                .andExpect(jsonPath("$[0].sentTransfers[0].amount").value(250.99))
                .andExpect(jsonPath("$[0].receivedTransfers").isEmpty())
                .andDo(print());
    }

    @Test
    void getAllReturnsEmptyList() throws Exception {
        transferRepository.deleteAll();
        customerRepository.deleteAll();

        mockMvc.perform(get("/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());
    }

    @Test
    void findByAccountNumber() throws Exception {
        var account = "00001-1";
        mockMvc.perform(get("/v1/customers/search?account=" + account))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro"))
                .andExpect(jsonPath("$.account").value(account))
                .andDo(print());
    }

    @Test
    void findByAccountNumberNotFound() throws Exception {
        mockMvc.perform(get("/v1/customers/search?account=00000-0"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void create() throws Exception {
        var account = "00009-9";
        var request = new CustomerDTO("test", account, BigDecimal.ZERO);

        mockMvc.perform(post("/v1/customers")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.account").value(account))
                .andDo(print());

        var savedCustomer = customerRepository.findByAccount(account);
        assertTrue(savedCustomer.isPresent());
        assertEquals("test", savedCustomer.get().getName());
    }

    @Test
    void createFailsWithNegativeBalance() throws Exception {
        var request = new CustomerDTO("Test", "12345-6", BigDecimal.valueOf(-10.00));

        mockMvc.perform(post("/v1/customers")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Balance must be positive"))
                .andDo(print());
    }

    @Test
    void createFailsWithInvalidAccountFormat() throws Exception {
        var request = new CustomerDTO("Test", "123-4", BigDecimal.ZERO);

        mockMvc.perform(post("/v1/customers")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(ACCOUNT_NUMBER))
                .andDo(print());
    }

    @Test
    void createFailsWhenAccountAlreadyExists() throws Exception {
        var request = new CustomerDTO("Pedro", "00001-1", BigDecimal.ZERO);

        mockMvc.perform(post("/v1/customers")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0]").value(UNIQUE_ACCOUNT))
                .andDo(print());
    }

    @Test
    void createFailsWhenNameIsMissing() throws Exception {
        var request = new CustomerDTO("", "00009-9", BigDecimal.ZERO);

        mockMvc.perform(post("/v1/customers")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Name is required"))
                .andDo(print());
    }

    @Test
    void getTransfers() throws Exception {
        var account = "00001-1";
        mockMvc.perform(get("/v1/customers/" + account + "/transfers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)))
                .andExpect(jsonPath("$[0].amount").isNumber())
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].sourceAccount").value(account))
                .andExpect(jsonPath("$[0].destinationAccount").isNotEmpty())
                .andExpect(jsonPath("$[?(@.status == 'Completed')]").exists())
                .andDo(print());
    }

    @Test
    void getTransfersReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/v1/customers/00000-0/transfers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());
    }

    @Test
    void transfer() throws Exception {
        var account = "00001-1";
        var destinationAccount = "00002-2";
        var transferRequest = new TransferDTO(destinationAccount, BigDecimal.valueOf(50.00));

        mockMvc.perform(post("/v1/customers/" + account + "/transfers")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody(transferRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sourceAccount").value(account))
                .andExpect(jsonPath("$.destinationAccount").value(destinationAccount))
                .andExpect(jsonPath("$.amount").value(50.00))
                .andExpect(jsonPath("$.status").value(TransferStatus.COMPLETED.toString()))
                .andDo(print());
    }

    @Test
    void transferFailsWhenAccountNotFound() throws Exception {
        var account = "00001-1";
        var unknownAccount = "00003-1";
        var transferRequest = new TransferDTO(unknownAccount, BigDecimal.valueOf(50.00));

        mockMvc.perform(post("/v1/customers/" + account + "/transfers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]").value(ACCOUNT_NOT_FOUND.formatted(unknownAccount)))
                .andDo(print());
    }

    @Test
    void transferFailsWithInsufficientFunds() throws Exception {
        var account = "00001-1";
        var destinationAccount = "00002-2";
        var transferRequest = new TransferDTO(destinationAccount, BigDecimal.valueOf(10000.00));

        var initialBalance = customerRepository.findByAccount(account).get().getBalance();
        assertEquals(new BigDecimal("100.00"), initialBalance);

        mockMvc.perform(post("/v1/customers/" + account + "/transfers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(INSUFFICIENT_FUNDS))
                .andDo(print());

        var finalBalance = customerRepository.findByAccount(account).get().getBalance();
        assertEquals(new BigDecimal("100.00"), finalBalance);
    }

    @Test
    void transferFailsWhenSourceIsSameAsDestination() throws Exception {
        var account = "00001-1";
        var transferRequest = new TransferDTO(account, BigDecimal.valueOf(10.00));

        mockMvc.perform(post("/v1/customers/" + account + "/transfers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(SAME_ACCOUNT))
                .andDo(print());
    }

    private String requestBody(Object body) {
        return objectMapper.writeValueAsString(body);
    }
}