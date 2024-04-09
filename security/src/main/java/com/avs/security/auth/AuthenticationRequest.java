package com.avs.security.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    private String userName;
    private String password;
    private String otp;
    private String firstName;
    private String lastName;
    private String mobile;
    private String email;
}
