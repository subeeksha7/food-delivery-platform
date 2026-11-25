
package com.fooddelivery.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRequest {

    @NotBlank(message = "Token is required")
    private String refreshToken;
}
