package com.breads.minds.config;

import com.breads.minds.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // Reference data accessible to all authenticated users
                        .requestMatchers(HttpMethod.GET, "/areas/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/districts/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/schools/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/health-programs/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/notices/**").authenticated()
                        // All authenticated users can view/update their own profile
                        .requestMatchers(HttpMethod.GET, "/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/{id}").authenticated()
                        // Admin-only modifications
                        .requestMatchers("/areas/**", "/districts/**").hasAnyRole("BREADS_COORDINATOR", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/schools/**").hasAnyRole("BREADS_COORDINATOR", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/schools/**").hasAnyRole("BREADS_COORDINATOR", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/schools/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/users/**").hasAnyRole("BREADS_COORDINATOR", "SUPER_ADMIN")
                        .requestMatchers("/notices/**").hasAnyRole("BREADS_COORDINATOR", "SUPER_ADMIN")
                        .requestMatchers("/system-logs/**").hasAnyRole("BREADS_COORDINATOR", "SUPER_ADMIN")
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
