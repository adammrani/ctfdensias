package com.ctfdensias.config;

import com.ctfdensias.model.Role;
import com.ctfdensias.model.User;
import com.ctfdensias.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates a default admin user on first startup if none exists.
 * Change the credentials immediately after first login!
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@ensias.ma");
                admin.setPassword("admin1234");   // BCrypt hashed automatically
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("✅ Default admin created: admin / admin1234  — CHANGE THIS PASSWORD!");
            }
        };
    }
}
