package com.example.spacecatsmarket.repository;

import com.example.spacecatsmarket.repository.entity.CustomerEntity;
import com.example.spacecatsmarket.repository.entity.OrderEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

}
