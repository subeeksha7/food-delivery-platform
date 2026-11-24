package com.fooddelivery.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.backend.dto.CustomerResponse;
import com.fooddelivery.backend.dto.RegisterCustomerRequest;
import com.fooddelivery.backend.service.CustomerService;



@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired 
    CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> addNewCustomer(@RequestBody RegisterCustomerRequest request){
    CustomerResponse saved=customerService.createCustomer(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    

}
