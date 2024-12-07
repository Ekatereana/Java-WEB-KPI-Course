package com.example.spacecatsmarket.service.impl;

import com.example.spacecatsmarket.common.CommunicationChannel;
import com.example.spacecatsmarket.domain.CustomerDetails;
import com.example.spacecatsmarket.dto.customer.CustomerDetailsDto;
import com.example.spacecatsmarket.repository.CustomerRepository;
import com.example.spacecatsmarket.repository.entity.CustomerEntity;
import com.example.spacecatsmarket.service.CustomerService;
import com.example.spacecatsmarket.service.exception.CustomerNotFoundException;
import com.example.spacecatsmarket.service.exception.PersistenceException;
import com.example.spacecatsmarket.service.mapper.CustomDetailsMapper;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomDetailsMapper customerDetailsMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDetails> getAllCustomerDetails() {
        return customerDetailsMapper.toCustomerDetailsList(customerRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDetails getCustomerDetailsById(UUID customerId) {
        CustomerEntity customer = customerRepository.findById(customerId).orElseThrow(() -> {
            log.info("Customer with id {} not found in mock", customerId);
            return new CustomerNotFoundException(customerId);
        });

        return customerDetailsMapper.toCustomerDetails(customer);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CustomerDetails createCustomerDetails(CustomerDetailsDto customerDetailsDto) {
        try {
            return customerDetailsMapper.toCustomerDetails(customerRepository.save(customerDetailsMapper.toCustomerEntity(customerDetailsDto)));
        } catch (Exception ex) {
            log.error("Exception occurred while saving customer details");
            throw new PersistenceException(ex);
        }
    }

    @Override
    @Transactional
    public void deleteCustomerDetailsById(UUID customerId) {
        try {
            customerRepository.deleteById(customerId);
        } catch (Exception ex) {
            log.error("Exception occurred while deleting customer details with id {}", customerId);
            throw new PersistenceException(ex);
        }

    }
}
