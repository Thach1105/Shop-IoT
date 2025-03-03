package com.thachnn.ShopIoT.service.impl;

import com.thachnn.ShopIoT.dto.request.ChangePasswordRequest;
import com.thachnn.ShopIoT.dto.request.CreateUserRequest;
import com.thachnn.ShopIoT.dto.request.UpdateUserRequest;
import com.thachnn.ShopIoT.dto.response.UserResponse;
import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.mapper.UserMapper;
import com.thachnn.ShopIoT.model.Role;
import com.thachnn.ShopIoT.model.User;
import com.thachnn.ShopIoT.repository.UserRepository;
import com.thachnn.ShopIoT.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final UserRepository userRepository;

    // Create user
    @Override
    @Transactional
    public User create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorApp.USERNAME_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorApp.EMAIL_EXISTED);
        }

        Role roleUSER = roleService.getByName("USER");
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(roleUSER);

        User newUser = userRepository.save(user);
        log.info("Create new user successfully");
        return newUser;
    }

    // Retrieve all users
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    // Retrieve user by ID
    @Override
    @PreAuthorize("hasRole('ADMIN') or #id == principal.claims['data']['id']")
    public User getById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.USER_NOTFOUND));
    }

    // Retrieve user by username
    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorApp.USER_NOTFOUND));
    }

    // Retrieve user by email
    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorApp.EMAIL_NOT_EXISTED));
    }

    // Check if email exists
    @Override
    public boolean existingEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Retrieve paginated users
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getPageUser(Integer pageNum, Integer size, String sortField, String keyword) {
        Sort sort = (sortField != null) ? Sort.by(sortField).ascending() : Sort.unsorted();
        Pageable pageable = PageRequest.of(pageNum, size, sort);

        Page<User> userPage;
        if (keyword == null) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository.findAllUser(keyword, pageable);
        }

        return userPage.map(userMapper::toUserResponse);
    }

    // Update user
    @Override
    @PreAuthorize("hasRole('ADMIN') or #id == principal.claims['data']['id']")
    @Transactional
    public UserResponse update(Integer id, UpdateUserRequest request) {
        User prevUser = getById(id);

        if (!prevUser.getEmail().equals(request.getEmail()) && existingEmail(request.getEmail())) {
            throw new AppException(ErrorApp.EMAIL_EXISTED);
        }

        User newUser = userMapper.toUserFromUpdateRequest(request);
        newUser.setId(prevUser.getId());
        newUser.setRole(prevUser.getRole());
        newUser.setUsername(prevUser.getUsername());
        newUser.setPassword(prevUser.getPassword());

        return userMapper.toUserResponse(userRepository.save(newUser));
    }

    // Delete user
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Integer id) {
        User user = getById(id);
        userRepository.delete(user);
    }

    // Change password
    @Override
    @PreAuthorize("#id == principal.claims['data']['id']")
    public void changePassword(Integer id, ChangePasswordRequest request) {
        User user = getById(id);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorApp.OLD_PASSWORD_INCORRECT);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
