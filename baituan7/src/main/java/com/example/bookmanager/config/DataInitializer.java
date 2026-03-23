package com.example.bookmanager.config;

import com.example.bookmanager.model.User;
import com.example.bookmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Admin
        userRepository.findByUsername("admin").ifPresentOrElse(
            existing -> {
                existing.setPassword(passwordEncoder.encode("admin123"));
                existing.setRole("ROLE_ADMIN");
                userRepository.save(existing);
            },
            () -> {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFullName("Quản Trị Viên");
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
            }
        );

        // User mặc định
        userRepository.findByUsername("user").ifPresentOrElse(
            existing -> {
                existing.setPassword(passwordEncoder.encode("user123"));
                existing.setRole("ROLE_USER");
                userRepository.save(existing);
            },
            () -> {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setFullName("Người Dùng");
                user.setRole("ROLE_USER");
                userRepository.save(user);
            }
        );
    }
}
