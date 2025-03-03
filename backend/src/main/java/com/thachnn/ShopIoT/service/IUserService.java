package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.dto.request.ChangePasswordRequest;
import com.thachnn.ShopIoT.dto.request.CreateUserRequest;
import com.thachnn.ShopIoT.dto.request.UpdateUserRequest;
import com.thachnn.ShopIoT.dto.response.UserResponse;
import com.thachnn.ShopIoT.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserService {
    public User create(CreateUserRequest request);

    public List<UserResponse> getAll() ;

    public User getById(Integer id) ;

    public User getByUsername(String username);

    public User getByEmail(String email);

    public boolean existingEmail(String email);

    public Page<UserResponse> getPageUser(Integer pageNum, Integer size, String sortField, String keyword);

    public UserResponse update(Integer id, UpdateUserRequest request);

    public void delete(Integer id);

    public void changePassword(Integer id, ChangePasswordRequest request);
}
