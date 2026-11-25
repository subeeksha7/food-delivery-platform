
package com.fooddelivery.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long accessTokenExpiresIn;
}
