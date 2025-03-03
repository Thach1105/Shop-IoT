package com.thachnn.ShopIoT.service.impl;

import com.thachnn.ShopIoT.exception.AppException;
import com.thachnn.ShopIoT.exception.ErrorApp;
import com.thachnn.ShopIoT.model.RedisToken;
import com.thachnn.ShopIoT.repository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTokenRepository redisTokenRepository;

    public void save(RedisToken redisToken){
        redisTokenRepository.save(redisToken);
    }

    public void delete(String id){
        redisTokenRepository.deleteById(id);
    }

    public boolean existsById(String id){
        return redisTokenRepository.existsById(id);
    }
}
