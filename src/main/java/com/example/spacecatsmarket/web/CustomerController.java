package com.example.spacecatsmarket.web;

import com.example.spacecatsmarket.dto.customer.CustomerDetailsDto;
import com.example.spacecatsmarket.dto.customer.CustomerDetailsListDto;
import com.example.spacecatsmarket.service.CustomerService;
import com.example.spacecatsmarket.service.mapper.CustomDetailsMapper;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.noContent;

@RestController
@Validated
@RequestMapping("/api/v1/admin/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomDetailsMapper customDetailsMapper;

    public CustomerController(CustomerService customerService, CustomDetailsMapper customDetailsMapper) {
        this.customerService = customerService;
        this.customDetailsMapper = customDetailsMapper;
    }

    @PreAuthorize("hasRole('COSMO_ADMIN')")
    @GetMapping
    public ResponseEntity<CustomerDetailsListDto> getAllCustomers() {
        return ResponseEntity.ok(customDetailsMapper.toCustomerDetailsListDto(customerService.getAllCustomerDetails()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDetailsDto> getCustomerById(@PathVariable UUID id) {
        return ResponseEntity.ok(customDetailsMapper.toCustomerDetailsDto(customerService.getCustomerDetailsById(id)));
    }

    @PostMapping
    public ResponseEntity<CustomerDetailsDto> createCustomer(@RequestBody @Valid CustomerDetailsDto customerDetailsDto){
        return ResponseEntity.ok(customerDetailsDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable UUID id){
        customerService.deleteCustomerDetailsById(id);
        return noContent().build();
    }
}
