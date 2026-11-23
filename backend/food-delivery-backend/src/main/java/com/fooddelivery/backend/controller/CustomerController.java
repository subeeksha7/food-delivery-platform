package com.fooddelivery.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fooddelivery.backend.entity.Customer;
import com.fooddelivery.backend.service.CustomerService;
import org.springframework.http.HttpStatus;



@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired 
    CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> addNewCustomer(@RequestBody Customer customer){
    Customer saved=customerService.createCustomer(customer);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    

}
