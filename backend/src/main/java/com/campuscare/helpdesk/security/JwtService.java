package com.campuscare.helpdesk.security;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.campuscare.helpdesk.entity.AppUser;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;

    public JwtService(JwtEncoder jwtEncoder,
                      @Value("${campuscare.jwt.expiration-seconds}") long expirationSeconds) {
        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(AppUser user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("campuscare-helpdesk-api")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .subject(user.getEmail())
                .claim("name", user.getName())
                .claim("role", user.getRole().name())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public long expirationSeconds() {
        return expirationSeconds;
    }
}
