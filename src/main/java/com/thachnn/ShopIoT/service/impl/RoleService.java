package com.thachnn.ShopIoT.service.impl;

import com.thachnn.ShopIoT.model.Role;
import com.thachnn.ShopIoT.repository.RoleRepository;
import com.thachnn.ShopIoT.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> getAll(){
        return  roleRepository.findAll();
    }

    @Override
    public Role getByName(String name){
        return roleRepository.findById(name).orElseThrow();
    }
}
