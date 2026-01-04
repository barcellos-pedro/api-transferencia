package com.itau.transferencia.controllers;

import com.itau.transferencia.entities.TransferStatus;
import com.itau.transferencia.http.requests.CustomerRequest;
import com.itau.transferencia.http.requests.TransferRequest;
import com.itau.transferencia.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

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

    @Test
    void getAll() throws Exception {
        mockMvc.perform(get("/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
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
    void create() throws Exception {
        var account = "00009-9";
        var request = new CustomerRequest("test", account, BigDecimal.ZERO);

        mockMvc.perform(post("/v1/customers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.account").value(account))
                .andDo(print());

        var savedCustomer = customerRepository.findByAccount(account);
        assertTrue(savedCustomer.isPresent());
        assertEquals("test", savedCustomer.get().getName());
    }

    @Test
    void getTransfers() throws Exception {
        var account = "00001-1";
        mockMvc.perform(get("/v1/customers/" + account + "/transfers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());
    }

    @Test
    void transfer() throws Exception {
        var account = "00001-1";
        var destinationAccount = "00002-2";
        var transferRequest = new TransferRequest(destinationAccount, BigDecimal.valueOf(50.00));

        mockMvc.perform(post("/v1/customers/" + account + "/transfers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sourceAccount").value(account))
                .andExpect(jsonPath("$.destinationAccount").value(destinationAccount))
                .andExpect(jsonPath("$.amount").value(50.00))
                .andExpect(jsonPath("$.status").value(TransferStatus.COMPLETED.toString()))
                .andDo(print());
    }
}