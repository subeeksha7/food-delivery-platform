package com.fooddelivery.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.backend.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    public boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
