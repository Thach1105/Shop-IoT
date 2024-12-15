package com.thachnn.ShopIoT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ShopIoTApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopIoTApplication.class, args);
	}

}
