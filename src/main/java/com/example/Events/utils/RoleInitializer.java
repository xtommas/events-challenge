package com.example.Events.utils;

import com.example.Events.models.Role;
import com.example.Events.models.User;
import com.example.Events.repositories.RoleRepository;
import com.example.Events.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${default.username}")
    private String defaultUsername;

    @Value("${default.password}")
    private String defaultPassword;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);
        }

        if (userRepository.findByUsername(defaultUsername).isEmpty()) {
            User defaultUser = new User();
            defaultUser.setUsername(defaultUsername);
            defaultUser.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));
            defaultUser.setRoles(Collections.singleton(roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("Role not found"))));
            userRepository.save(defaultUser);
        }
    }
}
