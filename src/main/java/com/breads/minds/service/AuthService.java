package com.breads.minds.service;

import com.breads.minds.dto.request.AuthRequest;
import com.breads.minds.dto.response.AuthResponse;
import com.breads.minds.entity.User;
import com.breads.minds.entity.enums.UserStatus;
import com.breads.minds.exception.UnauthorizedException;
import com.breads.minds.repository.UserRepository;
import com.breads.minds.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (user.getStatus() == UserStatus.PENDING) {
            throw new UnauthorizedException("Your account is awaiting admin approval");
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UnauthorizedException("Your account has been deactivated");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails, Map.of("role", user.getRole().name()));

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .districtId(user.getDistrict() != null ? user.getDistrict().getId() : null)
                .districtName(user.getDistrict() != null ? user.getDistrict().getName() : null)
                .build();
    }
}
