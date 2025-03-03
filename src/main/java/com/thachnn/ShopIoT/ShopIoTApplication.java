package com.thachnn.ShopIoT;

import com.thachnn.ShopIoT.model.OrderStatus;
import com.thachnn.ShopIoT.model.Role;
import com.thachnn.ShopIoT.model.User;
import com.thachnn.ShopIoT.repository.OrderStatusRepository;
import com.thachnn.ShopIoT.repository.RoleRepository;
import com.thachnn.ShopIoT.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
@EnableFeignClients
public class ShopIoTApplication {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private OrderStatusRepository orderStatusRepository;

	public static void main(String[] args) {
		SpringApplication.run(ShopIoTApplication.class, args);
	}

	@Bean
	public CommandLineRunner run() {
		return args -> {
			// Kiểm tra xem đã có role chưa, nếu chưa thì thêm
			if (roleRepository.count() == 0) {
				// Thêm role admin
				Role adminRole = Role.builder()
						.name("ADMIN")
						.description("Admin role with all privileges")
						.build();
				roleRepository.save(adminRole);

				// Thêm role user
				Role userRole = Role.builder()
						.name("USER")
						.description("User role with limited access")
						.build();
				roleRepository.save(userRole);

				log.info("Roles 'admin' and 'user' have been added to the database.");
			}

			if(userRepository.count() == 0){
				User admin = User.builder()
						.role(roleRepository.findById("ADMIN").orElseThrow())
						.fullName("admin")
						.username("admin")
						.password(passwordEncoder.encode("admin@123"))
						.email("shopiot.ptit@gmail.com")
						.build();

				userRepository.save(admin);
				log.info("Admin account created successfully with username: {} and password: {}", admin.getUsername(), "admin@123");
			}

			if(orderStatusRepository.count() == 0){
				OrderStatus pending = OrderStatus.builder()
						.statusName("PENDING")
						.description("The order has been placed but not yet processed.")
						.build();
				orderStatusRepository.save(pending);

				OrderStatus processing = OrderStatus.builder()
						.statusName("PROCESSING")
						.description("The order is currently being prepared or packed for shipment.")
						.build();
				orderStatusRepository.save(processing);

				OrderStatus shipped = OrderStatus.builder()
						.statusName("SHIPPED")
						.description("The order has been shipped and is on its way to the delivery address.")
						.build();
				orderStatusRepository.save(shipped);

				OrderStatus delivered = OrderStatus.builder()
						.statusName("DELIVERED")
						.description("The order has been successfully delivered to the customer.")
						.build();
				orderStatusRepository.save(delivered);

				OrderStatus cancelled = OrderStatus.builder()
						.statusName("CANCELLED")
						.description("The order has been cancelled and will not be processed or delivered.")
						.build();
				orderStatusRepository.save(cancelled);
			}
		};
	}
}
