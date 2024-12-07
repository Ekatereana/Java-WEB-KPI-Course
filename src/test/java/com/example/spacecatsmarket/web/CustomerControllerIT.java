package com.example.spacecatsmarket.web;

import com.example.spacecatsmarket.AbstractIt;
import com.example.spacecatsmarket.common.CommunicationChannel;
import com.example.spacecatsmarket.repository.CustomerRepository;
import com.example.spacecatsmarket.repository.entity.CustomerEntity;
import com.example.spacecatsmarket.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("Customer Controller IT")
class CustomerControllerIT extends AbstractIt {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @SpyBean
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        reset(customerService);
        customerRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "COSMO_ADMIN")
    @SneakyThrows
    void shouldGetAllCustomers() {
        saveCustomerEntity();
        mockMvc.perform(get("/api/v1/admin/customers"))
            .andExpect(status().isOk());

    }

    private void saveCustomerEntity() {
        customerRepository.save(CustomerEntity.builder()
            .customerReference(UUID.randomUUID())
            .name("Alice Johnson")
            .address("123 Cosmic Lane, Catnip City")
            .phoneNumber("123-456-7890")
            .email("alice@example.com")
            .communicationChannel(List.of(CommunicationChannel.HOLOGRAM))
            .build());
    }



}
