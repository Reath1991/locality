package com.avs.security.auth;

import com.avs.security.jwt.service.JwtService;
import com.avs.security.token.Token;
import com.avs.security.token.TokenRepository;
import com.avs.security.token.TokenType;
import com.avs.security.user.model.User;
import com.avs.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationResponse registerWithDetails(RegisterRequest request) {

        var isOtpValid =   otpService(request.getMobile(), request.getOtp());
        if(isOtpValid) {
            var findUser =  repository.findByUserName(request.getUserName()).isPresent();
            if(!findUser)
            {
                var user = User.builder()
                        .userName(request.getUserName())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .mobile(request.getMobile())
                        .role(request.getRole())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .build();

                var saveUser = repository.saveAndFlush(user);
                var jwtToken = jwtService.generateJwtToken(user);

                saveUserToken(saveUser, jwtToken);
                LOGGER.info("user {} register successfully  in database",request.getUserName());
                return AuthenticationResponse.builder().accessToken(jwtToken).status(AuthStatus.USER_REGISTER)
                        .build();
            }
            else {
                LOGGER.info("user {} details already exist in database",request.getUserName());
                return AuthenticationResponse.builder().status(AuthStatus.USER_NAME_ALREADY_EXIST)
                        .build();
            }
        }
        else {
            LOGGER.info("otp {} shared by user is not  valid",request.getOtp());
            return AuthenticationResponse.builder().status(AuthStatus.OTP_IS_NOT_VALID)
                    .build();
        }

    }

    private void saveUserToken(User saveUser, String jwtToken) {

        var  token = Token.builder()
                .token(jwtToken)
                .user(saveUser)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.saveAndFlush(token);

    }


    public AuthenticationResponse authenticateWithPasswordAndOtp(AuthenticationRequest request) {

        var isOtpValid = otpService(request.getUserName(), request.getOtp());
        if (isOtpValid) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));

                var user = repository.findByUserName(request.getUserName()).orElseThrow(() -> new UsernameNotFoundException("user name is not existing"));

                var jwtToken = jwtService.generateJwtToken(user);
                LOGGER.info("user {} is authenticate successfully ",request.getUserName());
                return AuthenticationResponse.builder().accessToken(jwtToken).status(AuthStatus.AUTHENTICATED_USER)
                        .build();
            }
            catch(AuthenticationException au)
            {
                LOGGER.info("Authentication exception occurred in time of Authenticate Username & password-> {}", au.toString());
                return AuthenticationResponse.builder().status(AuthStatus.USER_NOT_AUTHENTICATE)
                        .build();
            }
            catch (Exception e) {
                LOGGER.info("Exception come while extract  & validate the user detail from DB -> {}", e.toString());
                return AuthenticationResponse.builder().status(AuthStatus.INTERNAL_SERVER_ERROR)
                        .build();
            }
        } else {
            LOGGER.info("otp {} shared by user is not  valid",request.getOtp());
            return AuthenticationResponse.builder().status(AuthStatus.OTP_IS_NOT_VALID)
                    .build();
        }
    }

    public AuthenticationResponse authenticateWithOtp(AuthenticationRequest request) {

        var isOtpValid =  otpService(request.getUserName(), request.getOtp());
        if(isOtpValid) {
            try {
                LOGGER.info("otp {} shared by user is valid",request.getOtp());
                var findUserName = repository.findByUserName(request.getUserName());
                if (findUserName.isPresent()) {
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getUserName()));
                    var jwtToken = jwtService.generateJwtToken(findUserName.get());
                    LOGGER.info("user is  register with  us for username ->{}",request.getUserName());
                    return AuthenticationResponse.builder().status(AuthStatus.AUTHENTICATED_USER).accessToken(jwtToken)
                            .build();
                } else {
                    LOGGER.info("user is not register with  us for username ->{}",request.getUserName());
                    return AuthenticationResponse.builder().status(AuthStatus.USER_NOT_REGISTER)
                            .build();
                }
            }
            catch(Exception e)
            {
                LOGGER.info("Exception come while extract  & validate the user detail from DB -> {}", e.toString());
                return AuthenticationResponse.builder().status(AuthStatus.INTERNAL_SERVER_ERROR)
                        .build();
            }
        }
        else {
            LOGGER.info("otp {} shared by user is not  valid",request.getOtp());
            return AuthenticationResponse.builder().status(AuthStatus.OTP_IS_NOT_VALID)
                    .build();
        }

    }

    public AuthenticationResponse registerWithOtp(RegisterRequest request) {

        var isOtpValid =   otpService(request.getMobile(), request.getOtp());
        if(isOtpValid) {
            try {
                var mobile = request.getMobile();
                var findUser = repository.findByUserName(mobile).isPresent();
                if (!findUser) {
                    var user = User.builder()
                            .mobile(mobile)
                            .role(request.getRole())
                            .userName(request.getMobile())
                            .password(passwordEncoder.encode(request.getPassword() != null ? request.getPassword() : request.getMobile()))
                            .build();

                    var saveUser = repository.saveAndFlush(user);
                    var jwtToken = jwtService.generateJwtToken(user);

                    saveUserToken(saveUser, jwtToken);

                    return AuthenticationResponse.builder().accessToken(jwtToken).status(AuthStatus.USER_REGISTER)
                            .build();
                } else {
                    return AuthenticationResponse.builder().status(AuthStatus.USER_NAME_ALREADY_EXIST)
                            .build();
                }
            }
            catch(Exception e)
            {
                LOGGER.info("Exception come while save the user detail -> {}", e.toString());
                return AuthenticationResponse.builder().status(AuthStatus.INTERNAL_SERVER_ERROR)
                        .build();
            }
        }
        else {
            LOGGER.info("otp {} shared by user is not  valid",request.getOtp());
            return AuthenticationResponse.builder().status(AuthStatus.OTP_IS_NOT_VALID)
                    .build();
        }
    }

    private Boolean otpService(String mobile,String otp) {
        try {
            return Boolean.TRUE;
        }
        catch(Exception e)
        {
            LOGGER.info("there are some error while validating user otp {}", e.toString()) ;
            return Boolean.FALSE;
        }
    }
}
