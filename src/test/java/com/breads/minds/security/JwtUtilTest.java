package com.breads.minds.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey",
                "mySecretKeyForTestingPurposesOnlyThatIsLongEnough1234567890ABCDEF");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600000L);
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void generateToken_noExtraClaims_returnsValidToken() {
        String token = jwtUtil.generateToken(userDetails);
        assertThat(token).isNotBlank();
    }

    @Test
    void generateToken_withExtraClaims_returnsValidToken() {
        String token = jwtUtil.generateToken(userDetails, Map.of("role", "ADMIN"));
        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtUtil.generateToken(userDetails);
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    void isTokenValid_validToken_returnsTrue() {
        String token = jwtUtil.generateToken(userDetails);
        assertThat(jwtUtil.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_differentUser_returnsFalse() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails other = User.builder().username("other").password("x")
                .authorities(Collections.emptyList()).build();
        assertThat(jwtUtil.isTokenValid(token, other)).isFalse();
    }

    @Test
    void isTokenExpired_validToken_returnsFalse() {
        String token = jwtUtil.generateToken(userDetails);
        assertThat(jwtUtil.isTokenExpired(token)).isFalse();
    }

    @Test
    void isTokenExpired_expiredToken_throwsExpiredJwtException() {
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", -1000L);
        String token = jwtUtil.generateToken(userDetails);
        assertThatThrownBy(() -> jwtUtil.isTokenExpired(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void extractClaim_extractsSubjectCorrectly() {
        String token = jwtUtil.generateToken(userDetails);
        String subject = jwtUtil.extractClaim(token, Claims::getSubject);
        assertThat(subject).isEqualTo("testuser");
    }
}
