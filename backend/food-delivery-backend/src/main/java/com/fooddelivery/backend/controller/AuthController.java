
package com.fooddelivery.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.backend.dto.RefreshResponse;
import com.fooddelivery.backend.dto.TokenRequest;
import com.fooddelivery.backend.service.RefreshTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RefreshTokenService refreshTokenService;

    public AuthController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @Valid @RequestBody TokenRequest request) {

        RefreshResponse response =
                refreshTokenService.rotateRefreshToken(
                        request.getRefreshToken());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody TokenRequest request) {

        refreshTokenService.revokeRefreshToken(
                request.getRefreshToken());

        return ResponseEntity.noContent().build();
    }
}
