package com.avs.security.auth;
import com.avs.security.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String mobile;

    private String userName;

    private String password;

    private String otp;

    private Role role;

}
