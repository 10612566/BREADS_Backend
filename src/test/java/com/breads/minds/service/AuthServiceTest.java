package com.breads.minds.service;

import com.breads.minds.dto.request.AuthRequest;
import com.breads.minds.dto.response.AuthResponse;
import com.breads.minds.entity.District;
import com.breads.minds.entity.User;
import com.breads.minds.entity.enums.UserRole;
import com.breads.minds.entity.enums.UserStatus;
import com.breads.minds.exception.UnauthorizedException;
import com.breads.minds.repository.UserRepository;
import com.breads.minds.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private UserDetailsService userDetailsService;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private AuthService authService;

    private AuthRequest makeRequest(String username, String password) {
        AuthRequest r = new AuthRequest();
        r.setUsername(username);
        r.setPassword(password);
        return r;
    }

    private UserDetails springUserDetails(String username) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(username).password("p").authorities(Collections.emptyList()).build();
    }

    @Test
    void login_success_withDistrict_returnsAuthResponse() {
        District district = District.builder().id(10L).name("Dist1").build();
        User user = User.builder().id(1L).username("coord1").name("Coord One")
                .email("c@c.com").role(UserRole.DISTRICT_COORDINATOR).status(UserStatus.ACTIVE)
                .district(district).build();

        when(userRepository.findByUsername("coord1")).thenReturn(Optional.of(user));
        UserDetails ud = springUserDetails("coord1");
        when(userDetailsService.loadUserByUsername("coord1")).thenReturn(ud);
        when(jwtUtil.generateToken(eq(ud), anyMap())).thenReturn("jwt-token");

        AuthResponse resp = authService.login(makeRequest("coord1", "pass"));

        assertThat(resp.getToken()).isEqualTo("jwt-token");
        assertThat(resp.getTokenType()).isEqualTo("Bearer");
        assertThat(resp.getDistrictId()).isEqualTo(10L);
        assertThat(resp.getDistrictName()).isEqualTo("Dist1");
    }

    @Test
    void login_success_withoutDistrict_returnsNullDistrictFields() {
        User user = User.builder().id(1L).username("coord1").name("Test User")
                .email("t@t.com").role(UserRole.DISTRICT_COORDINATOR).status(UserStatus.ACTIVE).build();
        when(userRepository.findByUsername("coord1")).thenReturn(Optional.of(user));
        UserDetails ud = springUserDetails("coord1");
        when(userDetailsService.loadUserByUsername("coord1")).thenReturn(ud);
        when(jwtUtil.generateToken(eq(ud), anyMap())).thenReturn("tok");

        AuthResponse resp = authService.login(makeRequest("coord1", "pass"));

        assertThat(resp.getDistrictId()).isNull();
        assertThat(resp.getDistrictName()).isNull();
    }

    @Test
    void login_badCredentials_throwsUnauthorizedException() {
        doThrow(new BadCredentialsException("bad")).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> authService.login(makeRequest("x", "wrong")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void login_userNotFound_throwsUnauthorizedException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(makeRequest("ghost", "pass")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void login_pendingUser_throwsUnauthorizedException() {
        User user = User.builder().id(1L).username("pend").name("P")
                .role(UserRole.DISTRICT_COORDINATOR).status(UserStatus.PENDING).build();
        when(userRepository.findByUsername("pend")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(makeRequest("pend", "pass")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("awaiting admin approval");
    }

    @Test
    void login_inactiveUser_throwsUnauthorizedException() {
        User user = User.builder().id(1L).username("inact").name("I")
                .role(UserRole.DISTRICT_COORDINATOR).status(UserStatus.INACTIVE).build();
        when(userRepository.findByUsername("inact")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(makeRequest("inact", "pass")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("deactivated");
    }
}
