package com.thachnn.ShopIoT.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final UserRepository userRepository;

    public UserService(
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            RoleService roleService,
            UserRepository userRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.roleService = roleService;
        this.userRepository = userRepository;
    }

    // Create user
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

        return userRepository.save(user);
    }

    // Retrieve all users
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    // Retrieve user by ID
    @PreAuthorize("hasRole('ADMIN') or #id == principal.claims['data']['id']")
    public User getById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.USER_NOTFOUND));
    }

    // Retrieve user by username
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorApp.USER_NOTFOUND));
    }

    // Retrieve user by email
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorApp.EMAIL_NOT_EXISTED));
    }

    // Check if email exists
    public boolean existingEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Retrieve paginated users
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
    @PreAuthorize("hasRole('ADMIN') or #id == principal.claims['data']['id']")
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
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Integer id) {
        User user = getById(id);
        userRepository.delete(user);
    }

    // Change password
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
