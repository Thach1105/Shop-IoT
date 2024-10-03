package com.thachnn.ShopIoT.service;

import com.thachnn.ShopIoT.model.Role;
import com.thachnn.ShopIoT.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public List<Role> getAll(){
        return  roleRepository.findAll();
    }

    public Role getByName(String name){
        return roleRepository.findById(name).orElseThrow();
    }
}
