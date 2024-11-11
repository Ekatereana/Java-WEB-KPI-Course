package com.example.spacecatsmarket.repository;

import com.example.spacecatsmarket.repository.entity.CustomerEntity;
import java.util.UUID;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}