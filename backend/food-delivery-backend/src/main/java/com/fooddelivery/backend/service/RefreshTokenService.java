
package com.fooddelivery.backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fooddelivery.backend.dto.RefreshResponse;
import com.fooddelivery.backend.entity.Customer;
import com.fooddelivery.backend.entity.RefreshToken;
import com.fooddelivery.backend.exception.InvalidRefreshTokenException;
import com.fooddelivery.backend.repository.RefreshTokenRepository;
import com.fooddelivery.backend.security.JwtService;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long accessTokenExpiration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            @Value("${jwt.expiration}") long accessTokenExpiration) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.accessTokenExpiration = accessTokenExpiration;
    }

    // Creates a refresh token for a new login session
    @Transactional
    public String createRefreshToken(Customer customer) {

        String tokenFamily = UUID.randomUUID().toString();

        return createRefreshToken(customer, tokenFamily);
    }

    // Creates a refresh token belonging to an existing token family
    private String createRefreshToken(
            Customer customer,
            String tokenFamily) {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setCustomer(customer);
        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setTokenFamily(tokenFamily);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    // Refresh token rotation
    @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
    public RefreshResponse rotateRefreshToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token is required");
        }

        String tokenHash = hashToken(rawToken);

        RefreshToken oldToken = refreshTokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() -> new InvalidRefreshTokenException(
                        "Invalid refresh token"));

        LocalDateTime now = LocalDateTime.now();

        // Reuse detection
        if (oldToken.getRevokedAt() != null) {

            revokeTokenFamily(oldToken.getTokenFamily(), now);

            throw new InvalidRefreshTokenException(
                    "Refresh token reuse detected");
        }

        // Expiration check
        if (!oldToken.getExpiresAt().isAfter(now)) {

            oldToken.setRevokedAt(now);

            throw new InvalidRefreshTokenException(
                    "Refresh token has expired");
        }

        // Revoke the token that was used
        oldToken.setRevokedAt(now);

        // Generate replacement token in the same family
        String newRefreshToken = createRefreshToken(
                oldToken.getCustomer(),
                oldToken.getTokenFamily());

        // Generate a fresh access token
        String newAccessToken = jwtService.generateToken(
                oldToken.getCustomer().getCustomerId());

        return new RefreshResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                accessTokenExpiration / 1000
        );
    }

    // Logout / revoke a refresh token
    @Transactional
    public void revokeRefreshToken(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        refreshTokenRepository
                .findByTokenHashForUpdate(hashToken(rawToken))
                .ifPresent(token -> {

                    if (token.getRevokedAt() == null) {
                        token.setRevokedAt(LocalDateTime.now());
                    }
                });
    }

    // Revoke every token belonging to the same session family
    private void revokeTokenFamily(
            String tokenFamily,
            LocalDateTime revokedAt) {

        refreshTokenRepository
                .findByTokenFamily(tokenFamily)
                .forEach(token -> {

                    if (token.getRevokedAt() == null) {
                        token.setRevokedAt(revokedAt);
                    }
                });
    }

    public String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to hash refresh token", e);
        }
    }
}
