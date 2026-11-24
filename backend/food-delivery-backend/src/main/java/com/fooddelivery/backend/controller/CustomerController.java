package com.fooddelivery.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.backend.dto.CustomerResponse;
import com.fooddelivery.backend.dto.RegisterCustomerRequest;
import com.fooddelivery.backend.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> addNewCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        CustomerResponse saved = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

}
