
package com.fooddelivery.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private Long customerId;
    private String name;
    private String email;

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long accessTokenExpiresIn;
}
