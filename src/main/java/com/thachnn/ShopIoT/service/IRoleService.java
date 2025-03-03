package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.model.Role;

import java.util.List;

public interface IRoleService {

    public List<Role> getAll();

    public Role getByName(String name);
}
