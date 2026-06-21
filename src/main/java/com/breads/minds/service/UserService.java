package com.breads.minds.service;

import com.breads.minds.dto.request.UserCreateRequest;
import com.breads.minds.dto.request.UserUpdateRequest;
import com.breads.minds.dto.response.UserDTO;
import com.breads.minds.entity.District;
import com.breads.minds.entity.User;
import com.breads.minds.entity.enums.UserStatus;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.DistrictRepository;
import com.breads.minds.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;
    private final PasswordEncoder passwordEncoder;
    private final SystemLogService systemLogService;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    public UserDTO getUserById(Long id) {
        return toDTO(findUserOrThrow(id));
    }

    public UserDTO getUserByUsername(String username) {
        return toDTO(userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username)));
    }

    @Transactional
    public UserDTO createUser(UserCreateRequest request, String createdBy) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .build();

        if (request.getDistrictId() != null) {
            District district = districtRepository.findById(request.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found: " + request.getDistrictId()));
            user.setDistrict(district);
        }

        User saved = userRepository.save(user);
        systemLogService.log(createdBy, "CREATE_USER", "Created user: " + saved.getUsername());
        return toDTO(saved);
    }

    @Transactional
    public UserDTO updateOwnProfile(UserUpdateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (request.getName()     != null) user.setName(request.getName());
        if (request.getEmail()    != null) user.setEmail(request.getEmail());
        if (request.getMobile()   != null) user.setMobile(request.getMobile());
        if (request.getPhotoUrl() != null) user.setPhotoUrl(request.getPhotoUrl());
        // status and districtId ignored for self-update (admin-only fields)

        systemLogService.log(username, "UPDATE_OWN_PROFILE", "User updated own profile: " + username);
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUser(Long id, UserUpdateRequest request, String updatedBy) {
        User user = findUserOrThrow(id);

        if (request.getName()      != null) user.setName(request.getName());
        if (request.getEmail()     != null) user.setEmail(request.getEmail());
        if (request.getMobile()    != null) user.setMobile(request.getMobile());
        if (request.getStatus()    != null) user.setStatus(request.getStatus());
        if (request.getPhotoUrl()  != null) user.setPhotoUrl(request.getPhotoUrl());

        if (request.getDistrictId() != null) {
            District district = districtRepository.findById(request.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found"));
            user.setDistrict(district);
        }

        systemLogService.log(updatedBy, "UPDATE_USER", "Updated user: " + user.getUsername());
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO approveUser(Long id, String approvedBy) {
        User user = findUserOrThrow(id);
        user.setStatus(UserStatus.ACTIVE);
        systemLogService.log(approvedBy, "APPROVE_USER", "Approved user: " + user.getUsername());
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id, String deletedBy) {
        User user = findUserOrThrow(id);
        systemLogService.log(deletedBy, "DELETE_USER", "Deleted user: " + user.getUsername());
        userRepository.delete(user);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private UserDTO toDTO(User u) {
        return UserDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .name(u.getName())
                .email(u.getEmail())
                .mobile(u.getMobile())
                .role(u.getRole())
                .status(u.getStatus())
                .districtId(u.getDistrict() != null ? u.getDistrict().getId() : null)
                .districtName(u.getDistrict() != null ? u.getDistrict().getName() : null)
                .photoUrl(u.getPhotoUrl())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
