package com.breads.minds.service;

import com.breads.minds.dto.request.UserCreateRequest;
import com.breads.minds.dto.request.UserUpdateRequest;
import com.breads.minds.dto.response.UserDTO;
import com.breads.minds.entity.District;
import com.breads.minds.entity.User;
import com.breads.minds.entity.enums.UserRole;
import com.breads.minds.entity.enums.UserStatus;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.DistrictRepository;
import com.breads.minds.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private DistrictRepository districtRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SystemLogService systemLogService;
    @InjectMocks private UserService userService;

    private User buildUser(Long id, String username, UserStatus status) {
        return User.builder()
                .id(id).username(username).name("Test " + username)
                .email(username + "@test.com").mobile("1234567890")
                .role(UserRole.DISTRICT_COORDINATOR).status(status)
                .createdAt(LocalDateTime.now()).build();
    }

    @Test
    void getAllUsers_returnsListOfDTOs() {
        when(userRepository.findAll()).thenReturn(List.of(buildUser(1L, "u1", UserStatus.ACTIVE)));
        List<UserDTO> result = userService.getAllUsers();
        assertThat(result).hasSize(1);
    }

    @Test
    void getUserById_found_returnsDTO() {
        User user = buildUser(1L, "u1", UserStatus.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDTO dto = userService.getUserById(1L);
        assertThat(dto.getId()).isEqualTo(1L);
    }

    @Test
    void getUserById_notFound_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getUserByUsername_found_returnsDTO() {
        User user = buildUser(1L, "alice", UserStatus.ACTIVE);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        assertThat(userService.getUserByUsername("alice").getUsername()).isEqualTo("alice");
    }

    @Test
    void getUserByUsername_notFound_throwsResourceNotFoundException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserByUsername("ghost"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createUser_success_withDistrict() {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("newuser"); req.setPassword("pass123");
        req.setName("New User"); req.setEmail("new@test.com");
        req.setRole(UserRole.DISTRICT_COORDINATOR); req.setDistrictId(1L);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        District district = District.builder().id(1L).name("D1").build();
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district));
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        User saved = buildUser(10L, "newuser", UserStatus.ACTIVE);
        saved.setDistrict(district);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDTO dto = userService.createUser(req, "admin");
        assertThat(dto.getDistrictId()).isEqualTo(1L);
    }

    @Test
    void createUser_success_withoutDistrictAndEmail() {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("newuser2"); req.setPassword("pass123");
        req.setName("New User2"); req.setEmail(null);
        req.setRole(UserRole.SUPER_ADMIN); req.setDistrictId(null);

        when(userRepository.existsByUsername("newuser2")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        User saved = buildUser(11L, "newuser2", UserStatus.ACTIVE);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDTO dto = userService.createUser(req, "admin");
        assertThat(dto).isNotNull();
    }

    @Test
    void createUser_duplicateUsername_throwsIllegalArgumentException() {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("dupe"); req.setPassword("p"); req.setName("D");
        req.setRole(UserRole.DISTRICT_COORDINATOR);
        when(userRepository.existsByUsername("dupe")).thenReturn(true);
        assertThatThrownBy(() -> userService.createUser(req, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    void createUser_duplicateEmail_throwsIllegalArgumentException() {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("unique"); req.setPassword("p"); req.setName("U");
        req.setEmail("dup@test.com"); req.setRole(UserRole.DISTRICT_COORDINATOR);
        when(userRepository.existsByUsername("unique")).thenReturn(false);
        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);
        assertThatThrownBy(() -> userService.createUser(req, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void createUser_districtNotFound_throwsResourceNotFoundException() {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("u"); req.setPassword("p"); req.setName("U");
        req.setEmail("u@u.com"); req.setRole(UserRole.DISTRICT_COORDINATOR); req.setDistrictId(999L);
        when(userRepository.existsByUsername("u")).thenReturn(false);
        when(userRepository.existsByEmail("u@u.com")).thenReturn(false);
        when(districtRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.createUser(req, "admin"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateOwnProfile_allFieldsNull_stillSaves() {
        User user = buildUser(1L, "alice", UserStatus.ACTIVE);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.updateOwnProfile(new UserUpdateRequest(), "alice");
        verify(userRepository).save(user);
    }

    @Test
    void updateOwnProfile_allFieldsSet_updatesUser() {
        User user = buildUser(1L, "alice", UserStatus.ACTIVE);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserUpdateRequest req = new UserUpdateRequest();
        req.setName("Alice Updated"); req.setEmail("new@a.com");
        req.setMobile("9876543210"); req.setPhotoUrl("http://photo.url");
        userService.updateOwnProfile(req, "alice");

        assertThat(user.getName()).isEqualTo("Alice Updated");
        assertThat(user.getEmail()).isEqualTo("new@a.com");
        assertThat(user.getMobile()).isEqualTo("9876543210");
        assertThat(user.getPhotoUrl()).isEqualTo("http://photo.url");
    }

    @Test
    void updateOwnProfile_userNotFound_throwsResourceNotFoundException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateOwnProfile(new UserUpdateRequest(), "ghost"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateUser_allFieldsSet_withDistrict() {
        User user = buildUser(1L, "u", UserStatus.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        District district = District.builder().id(2L).name("D2").build();
        when(districtRepository.findById(2L)).thenReturn(Optional.of(district));
        when(userRepository.save(user)).thenReturn(user);

        UserUpdateRequest req = new UserUpdateRequest();
        req.setName("Updated"); req.setEmail("u@u.com"); req.setMobile("9999");
        req.setStatus(UserStatus.INACTIVE); req.setPhotoUrl("http://p.url");
        req.setDistrictId(2L);
        userService.updateUser(1L, req, "admin");

        assertThat(user.getName()).isEqualTo("Updated");
        assertThat(user.getDistrict()).isEqualTo(district);
        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    void updateUser_allFieldsNull_noUpdates() {
        User user = buildUser(1L, "u", UserStatus.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        userService.updateUser(1L, new UserUpdateRequest(), "admin");
        verify(districtRepository, never()).findById(anyLong());
    }

    @Test
    void updateUser_districtNotFound_throwsResourceNotFoundException() {
        User user = buildUser(1L, "u", UserStatus.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(districtRepository.findById(99L)).thenReturn(Optional.empty());
        UserUpdateRequest req = new UserUpdateRequest();
        req.setDistrictId(99L);
        assertThatThrownBy(() -> userService.updateUser(1L, req, "admin"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void approveUser_setsStatusToActive() {
        User user = buildUser(1L, "pend", UserStatus.PENDING);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.approveUser(1L, "admin");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void deleteUser_deletesUser() {
        User user = buildUser(1L, "u", UserStatus.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(1L, "admin");
        verify(userRepository).delete(user);
    }

    @Test
    void getAllUsers_withDistrict_mapsDTOCorrectly() {
        District d = District.builder().id(5L).name("TestDist").build();
        User user = buildUser(1L, "u1", UserStatus.ACTIVE);
        user.setDistrict(d);
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDTO> dtos = userService.getAllUsers();
        assertThat(dtos.get(0).getDistrictId()).isEqualTo(5L);
        assertThat(dtos.get(0).getDistrictName()).isEqualTo("TestDist");
    }
}
