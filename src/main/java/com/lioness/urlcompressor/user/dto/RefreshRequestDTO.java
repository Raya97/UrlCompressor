package com.lioness.urlcompressor.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * RefreshRequestDTO is used when a user requests a new access token
 * by providing a valid refresh token.
 */
@Getter
@Setter
public class RefreshRequestDTO {

    /**
     * The refresh token issued during login/registration.
     * This field is mandatory and must not be blank.
     */
    @NotBlank(message = "Refresh token must not be blank")
    private String refreshToken;
}
