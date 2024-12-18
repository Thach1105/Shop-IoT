package com.thachnn.ShopIoT.api;

import com.thachnn.ShopIoT.dto.request.ChangePasswordRequest;
import com.thachnn.ShopIoT.dto.request.CreateUserRequest;
import com.thachnn.ShopIoT.dto.request.UpdateUserRequest;
import com.thachnn.ShopIoT.dto.response.ApiResponse;
import com.thachnn.ShopIoT.dto.response.UserResponse;
import com.thachnn.ShopIoT.mapper.UserMapper;
import com.thachnn.ShopIoT.model.User;
import com.thachnn.ShopIoT.service.UserService;
import com.thachnn.ShopIoT.util.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@EnableMethodSecurity
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody CreateUserRequest request){

        User newUser = userService.create(request);

        UserResponse userResponse = userMapper.toUserResponse(newUser);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(userResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable("id") Integer id){

        UserResponse userResponse = userMapper.toUserResponse(userService.getById(id));

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(userResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<?>> getMyInfo(@AuthenticationPrincipal Jwt jwt){

        String username = jwt.getSubject();
        User user = userService.getByUsername(username);
        UserResponse userResponse = userMapper.toUserResponse(user);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(userResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllUser(){

        List<UserResponse> list_user = userService.getAll();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(list_user)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUserPage(
        @RequestParam(name = "pageNumber", defaultValue = PageInfo.PAGE_NUMBER_DEFAULT) Integer pageNum,
        @RequestParam(name = "pageSize", defaultValue = PageInfo.PAGE_SIZE_DEFAULT) Integer pageSize,
        @RequestParam(name = "sort", required = false) String sortField,
        @RequestParam(name = "keyword", required = false) String keyword
    ){
        Page<UserResponse> page = userService.getPageUser(pageNum-1, pageSize, sortField, keyword);

        List<UserResponse> list_response = page.getContent();

        PageInfo pageInfo = PageInfo.builder()
                .page(page.getNumber()+1)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(list_response)
                .pageDetails(pageInfo)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateUserRequest request
            ){
        UserResponse userResponse = userService.update(id, request);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(userResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @PathVariable("id") Integer id,
            @RequestBody ChangePasswordRequest request
    ){
        userService.changePassword(id, request);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("Password has been changed successfully")
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable("id") Integer id
    ){
       userService.delete(id);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content("User account deleted successfully")
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }
}
