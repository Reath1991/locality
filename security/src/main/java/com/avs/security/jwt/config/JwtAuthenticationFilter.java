package com.avs.security.jwt.config;

import com.avs.security.jwt.service.JwtService;
import com.avs.security.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    private final static Logger  LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // fetch the auth header value from request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if(authHeader == null  || !authHeader.startsWith("Bearer "))
        {
            LOGGER.info("Either authHeader  value is not set or Authentication type not BEARER");
            filterChain.doFilter(request,response);
            return ;
        }
        try {
            // fetch the jwt token from header
            jwt = authHeader.substring(7);
            // validate the token
            String userName = jwtService.extractUsername(jwt);
            // to check is user is already authenticated . this information is store in SecurityContext Holder
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {

                    UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

                    // check token is valid and exist in db
                    var isTokenValid = tokenRepository.findByToken(jwt).map(token -> !token.isExpired() && !token.isRevoked()).orElse(false);
                    // check again still userToken is valid
                    if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (UsernameNotFoundException ue) {
                    LOGGER.info("user detail not found corresponding user associate with JWt token  : {}", ue.getMessage());
                    response.setStatus(403);
                    filterChain.doFilter(request, response);
                    return;

                }

            }
        }
        catch(Exception e)
        {
            LOGGER.info("something wrong in time of extract & validate jwt token  : {}", e.getMessage());
            response.setStatus(403);
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request,response);

    }
}
