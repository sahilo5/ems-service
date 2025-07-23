package com.SadhyaSiddhi.ems_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.SadhyaSiddhi.ems_service.security.SecurityConstants.JWT_EXPIRATION;
import static com.SadhyaSiddhi.ems_service.security.SecurityConstants.JWT_SECRET;

@Component
public class JWTGenerator {
    public String generateToken(Authentication authentication){
        String username= authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRATION);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256,JWT_SECRET)
                .compact();

        return token;
    }

    public String getUserNameFromJWT(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        }catch(Exception e){
            throw new AuthenticationCredentialsNotFoundException("JWT was Expired or Incorrect");
        }
    }
}
