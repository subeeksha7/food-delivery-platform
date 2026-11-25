package com.fooddelivery.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fooddelivery.backend.dto.CustomerResponse;
import com.fooddelivery.backend.dto.LoginRequest;
import com.fooddelivery.backend.dto.LoginResponse;
import com.fooddelivery.backend.dto.RegisterCustomerRequest;
import com.fooddelivery.backend.entity.Customer;
import com.fooddelivery.backend.exception.EmailAlreadyExistsException;
import com.fooddelivery.backend.exception.InvalidCredentialsException;
import com.fooddelivery.backend.exception.PhoneAlreadyExistsException;
import com.fooddelivery.backend.repository.CustomerRepository;
import com.fooddelivery.backend.security.JwtService;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private final EntityManager entityManager;

    public CustomerService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            EntityManager entityManager) {

        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.entityManager = entityManager;
    }

    @Transactional
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

        entityManager.refresh(customer);

        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setCustomerId(customer.getCustomerId());
        customerResponse.setName(customer.getName());
        customerResponse.setEmail(customer.getEmail());
        customerResponse.setPhone(customer.getPhone());
        customerResponse.setCreatedAt(customer.getCreatedAt());

        return customerResponse;
    }

    public LoginResponse login(LoginRequest request) {

        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(), customer.getPassword())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }

        // Generate short-lived access token
        String accessToken
                = jwtService.generateToken(customer.getCustomerId());

        // Generate long-lived refresh token and store its hash
        String refreshToken
                = refreshTokenService.createRefreshToken(customer);

        LoginResponse response = new LoginResponse();

        response.setCustomerId(customer.getCustomerId());
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setAccessTokenExpiresIn(900);

        return response;
    }

}
