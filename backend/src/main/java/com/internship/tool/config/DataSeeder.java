package com.internship.tool.config;

import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() >= 30) {
                return;
            }

            List<User> users = new ArrayList<>();
            users.add(createUser("Admin User", "admin@example.com", "admin123", "ADMIN", true));

            for (int index = 1; index <= 29; index++) {
                String number = String.format("%02d", index);
                users.add(createUser(
                        "Team Member " + number,
                        "member" + number + "@example.com",
                        "password" + number,
                        "USER",
                        index % 5 != 0
                ));
            }

            for (User user : users) {
                if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
                    userRepository.save(user);
                }
            }
        };
    }

    private User createUser(String name, String email, String password, String role, Boolean active) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setIsActive(active);
        return user;
    }
}
