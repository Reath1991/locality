package com.avs.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register-with-details")
    public ResponseEntity<AuthenticationResponse> registerWithDetails(@RequestBody RegisterRequest request)
    {

        AuthenticationResponse response = authenticationService.registerWithDetails(request);
        if(response.getStatus().contains("FAILED")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

     // register with  mobile/Email id
    @PostMapping("/register-with-otp")
    public ResponseEntity<AuthenticationResponse> registerWithOtp(@RequestBody RegisterRequest request)
    {
        AuthenticationResponse response = authenticationService.registerWithOtp(request);
        if(response.getStatus().contains("FAILED")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/otp-authentication")
    public ResponseEntity<AuthenticationResponse> authenticateWithOtp(@RequestBody AuthenticationRequest request)
    {
        AuthenticationResponse response = authenticationService.authenticateWithOtp(request);
        if(response.getStatus().contains("FAILED")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/two-factor-authentication")
    public ResponseEntity<AuthenticationResponse> authenticateWithPasswordAndOtp(@RequestBody AuthenticationRequest request)
    {
        AuthenticationResponse response = authenticationService.authenticateWithPasswordAndOtp(request);
        if(response.getStatus().contains("FAILED")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
