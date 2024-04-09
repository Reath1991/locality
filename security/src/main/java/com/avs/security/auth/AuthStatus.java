package com.avs.security.auth;

public class AuthStatus {

    public static final String  OTP_IS_NOT_VALID = "FAILED|OTP_IS_NOT_VALID";
    public static final String  INTERNAL_SERVER_ERROR = "FAILED|INTERNAL_SERVER_ERROR";
    public static final String USER_NAME_ALREADY_EXIST =  "FAILED|USER_NAME_ALREADY_EXIST";
    public static final String USER_REGISTER = "SUCCESS|USER_REGISTER";
    public static final String AUTHENTICATED_USER = "SUCCESS|AUTHENTICATED_USER";

    public static final String USER_NOT_AUTHENTICATE = "FAILED|USER_NOT_AUTHENTICATE";

    public static final String USER_NOT_REGISTER="FAILED|USER_NOT_REGISTER";
}
