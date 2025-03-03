package com.thachnn.ShopIoT.mapper;

import com.thachnn.ShopIoT.dto.request.CreateUserRequest;
import com.thachnn.ShopIoT.dto.request.UpdateUserRequest;
import com.thachnn.ShopIoT.dto.response.UserResponse;
import com.thachnn.ShopIoT.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(CreateUserRequest request);

    UserResponse toUserResponse(User user);

    User toUserFromUpdateRequest(UpdateUserRequest request);
}
