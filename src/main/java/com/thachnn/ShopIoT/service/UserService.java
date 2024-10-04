package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.CreateUserRequest;
import com.thachnn.ShopIoT.dto.request.UpdateUserRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    public User create(CreateUserRequest request){
        Role roleUSER = roleService.getByName("USER");
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(roleUSER);

        if(userRepository.existsByUsername(request.getUsername())) throw new AppException(ErrorApp.USERNAME_EXISTED);
        if(userRepository.existsByEmail(request.getEmail())) throw new AppException(ErrorApp.EMAIL_EXISTED);

        return userRepository.save(user);
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public User getById(Integer id){
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorApp.USER_NOTFOUND));

    }

    public User getByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorApp.USER_NOTFOUND));
    }

    public User getByEmail(String email){
        return  userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorApp.EMAIL_NOT_EXISTED));
    }

    public Page<User> getPageUser(Integer pageNum, Integer size, String sortField, String keyword){
        Sort sort = sortField != null ? Sort.by(sortField).ascending() : Sort.unsorted();
        Pageable pageable = PageRequest.of(pageNum, size, sort);

        if(keyword == null){
            return userRepository.findAll(pageable);
        } else {
            return userRepository.findAllUser(keyword, pageable);
        }
    }

    public User update(Integer id, UpdateUserRequest request){

        User prevUser = getById(id);

        User newUser = userMapper.toUserFromUpdateRequest(request);
        newUser.setId(prevUser.getId());
        newUser.setRole(prevUser.getRole());
        newUser.setUsername(prevUser.getUsername());
        newUser.setPassword(prevUser.getPassword());

        return userRepository.save(newUser);
    }

    public void delete(Integer id){
        User user = getById(id);

        userRepository.deleteById(id);
    }
}
