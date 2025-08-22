package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.exceptions.InvalidQrTokenException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class QrTokenService {

    @Value("${security.qr-secret}")
    private String QR_SECRET;

    // Generate short-lived QR token (15 sec validity)
    public String generateQrToken() {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject("attendance-qr")
                .setId(UUID.randomUUID().toString())
                .claim("slot", now.getEpochSecond() / 15) // rotates every 15s
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(15)))
                .signWith(SignatureAlgorithm.HS256, QR_SECRET)
                .compact();
    }

    // Validate scanned QR token
    public void validateQrToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(QR_SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            if (!"attendance-qr".equals(claims.getSubject())) {
                throw new InvalidQrTokenException("Invalid QR token type");
            }
        } catch (JwtException e) {
            throw new InvalidQrTokenException("Expired or malformed QR token");
        }
    }
}
