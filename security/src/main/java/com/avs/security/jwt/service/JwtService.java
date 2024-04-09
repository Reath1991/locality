package com.avs.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long expiration;

    // extract the userName from token
    public String extractUsername(String jwt) {
        return extractClaim(jwt,Claims::getSubject);
    }

    //  verify the claims
    public<T> T extractClaim(String token , Function<Claims,T> claimResolver)
    {
        final Claims allclaims = extractAllClaims(token);
         return  claimResolver.apply(allclaims);
    }

    // extract all claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // to generate A signingKey
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // generate the Jwt Token
    public String generateJwtToken(Map<String,Object> extraClaims , UserDetails userDetails)
    {
        return buildToken(extraClaims, userDetails);

    }

    public String generateJwtToken(UserDetails userDetails)
    {
        return buildToken(new HashMap<>(), userDetails);

    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public Boolean isTokenValid(String token, UserDetails userDetails)
    {
        final String  userName = extractUsername(token);
        return  userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
         return extractTokenExpiration(token).before(new Date());
    }

    private Date extractTokenExpiration(String token) {

        return  extractClaim(token,Claims::getExpiration);

    }

    // generate the Jwt Token
    public String refreshJwtToken(UserDetails userDetails)
    {
        return buildToken(new HashMap<String,Object>(), userDetails);

    }

}
