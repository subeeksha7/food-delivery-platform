package com.fooddelivery.backend.service;

import org.springframework.stereotype.Service;

import com.fooddelivery.backend.entity.Customer;
import com.fooddelivery.backend.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer) {

        String email = customer.getEmail();
        boolean exists = customerRepository.existsByEmail(email);
        if (exists) {
            throw new RuntimeException(
                    "Email already exists. Please use a different email."
            );
        }

        return customerRepository.save(customer);
    }

}
