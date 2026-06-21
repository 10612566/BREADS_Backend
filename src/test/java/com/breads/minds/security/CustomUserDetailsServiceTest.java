package com.breads.minds.security;

import com.breads.minds.entity.User;
import com.breads.minds.entity.enums.UserRole;
import com.breads.minds.entity.enums.UserStatus;
import com.breads.minds.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_userFound_activeStatus_returnsUserDetails() {
        User user = User.builder().username("admin").password("enc_pass")
                .role(UserRole.SUPER_ADMIN).status(UserStatus.ACTIVE).build();
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("admin");

        assertThat(details.getUsername()).isEqualTo("admin");
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.getAuthorities()).hasSize(1);
        assertThat(details.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_SUPER_ADMIN");
    }

    @Test
    void loadUserByUsername_userFound_inactiveStatus_returnsLockedAccount() {
        User user = User.builder().username("coord").password("enc_pass")
                .role(UserRole.DISTRICT_COORDINATOR).status(UserStatus.INACTIVE).build();
        when(userRepository.findByUsername("coord")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("coord");

        assertThat(details.isAccountNonLocked()).isFalse();
    }

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("ghost");
    }

    @Test
    void loadUserByUsername_userFound_pendingStatus_returnsNonLockedAccount() {
        User user = User.builder().username("newuser").password("enc_pass")
                .role(UserRole.DISTRICT_COORDINATOR).status(UserStatus.PENDING).build();
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("newuser");

        assertThat(details.isAccountNonLocked()).isTrue();
    }
}
