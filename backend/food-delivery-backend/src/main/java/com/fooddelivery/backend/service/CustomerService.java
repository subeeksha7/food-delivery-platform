package com.fooddelivery.backend.service;

import org.springframework.stereotype.Service;

import com.fooddelivery.backend.dto.CustomerResponse;
import com.fooddelivery.backend.dto.RegisterCustomerRequest;
import com.fooddelivery.backend.entity.Customer;
import com.fooddelivery.backend.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse createCustomer(RegisterCustomerRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                    "Email already exists. Please use a different email."
            );
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setPassword(request.getPassword());
        customer = customerRepository.save(customer);

        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setCustomerId(customer.getCustomerId());
        customerResponse.setName(customer.getName());
        customerResponse.setEmail(customer.getEmail());
        customerResponse.setPhone(customer.getPhone());
        customerResponse.setCreatedAt(customer.getCreatedAt());

        return customerResponse;
    }

}
