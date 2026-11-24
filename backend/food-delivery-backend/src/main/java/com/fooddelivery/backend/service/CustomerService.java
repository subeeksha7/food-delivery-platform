package com.fooddelivery.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fooddelivery.backend.dto.CustomerResponse;
import com.fooddelivery.backend.dto.RegisterCustomerRequest;
import com.fooddelivery.backend.entity.Customer;
import com.fooddelivery.backend.exception.EmailAlreadyExistsException;
import com.fooddelivery.backend.exception.PhoneAlreadyExistsException;
import com.fooddelivery.backend.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomerResponse createCustomer(RegisterCustomerRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already exists. Please use a different email."
            );
        }
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new PhoneAlreadyExistsException(
                    "Phone number already exists."
            );
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
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
