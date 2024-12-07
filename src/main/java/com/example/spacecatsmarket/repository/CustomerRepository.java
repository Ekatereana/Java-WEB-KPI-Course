package com.example.spacecatsmarket.repository;

import com.example.spacecatsmarket.repository.entity.CustomerEntity;
import com.example.spacecatsmarket.repository.projection.CustomerDetailsProjection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends NaturalIdRepository<CustomerEntity, UUID> {

    Optional<CustomerEntity> findDistinctByPhoneNumberOrderByNameAsc(String phoneNumber);

    Optional<CustomerDetailsProjection> findDistinctByEmail(String email);
}
