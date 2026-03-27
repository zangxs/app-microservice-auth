package com.brayanspv.auth.service.implementations;

import com.brayanspv.auth.repositories.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {
    private JWTService jwtService;

    private final String secret = "MsUEV8IkLNB7NkFB0iVHtaCNrn98CTzYIz/5w4te3J0=";
    private final Long expiration = 86400000L;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expiration", expiration);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("daniel@gmail.com");
        user.setUsername("daniel");

        String token = jwtService.generateToken(user);

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_shouldContainCorrectClaims() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("daniel@gmail.com");
        user.setUsername("daniel");

        String token = jwtService.generateToken(user);

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Assertions.assertEquals("1", claims.getSubject());
        Assertions.assertEquals("daniel@gmail.com", claims.get("email"));
        Assertions.assertNotNull(claims.getIssuedAt());
        Assertions.assertNotNull(claims.getExpiration());
    }


}