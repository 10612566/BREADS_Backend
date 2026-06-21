package com.breads.minds.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock private JwtUtil jwtUtil;
    @Mock private UserDetailsService userDetailsService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userDetails = User.builder().username("user1").password("pass")
                .authorities(Collections.emptyList()).build();
    }

    @Test
    void doFilter_noAuthHeader_passesThrough() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void doFilter_authHeaderWithoutBearer_passesThrough() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void doFilter_invalidJwt_passesThrough() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidtoken");
        when(jwtUtil.extractUsername("invalidtoken")).thenThrow(new RuntimeException("bad token"));
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_validJwt_setsAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtUtil.extractUsername("validtoken")).thenReturn("user1");
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid("validtoken", userDetails)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user1");
    }

    @Test
    void doFilter_validJwtButTokenInvalid_doesNotSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtUtil.extractUsername("validtoken")).thenReturn("user1");
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid("validtoken", userDetails)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_usernameNull_doesNotLoadUserDetails() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.extractUsername("token")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }
}
