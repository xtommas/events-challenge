package com.example.Events.services;

import com.example.Events.dtos.AuthUserDto;
import com.example.Events.models.Role;
import com.example.Events.models.User;
import com.example.Events.repositories.RoleRepository;
import com.example.Events.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
    }

    public User register(AuthUserDto input) {
        User user = new User();
        user.setUsername(input.getUsername());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        Optional<Role> optionalRoleUser = roleRepository.findByName("USER");
        Role roleUser;
        if (optionalRoleUser.isEmpty()) {
            roleUser = new Role();
            roleUser.setName("USER");
            roleRepository.save(roleUser);
        } else {
            roleUser = optionalRoleUser.get();
        }
        user.setRoles(Set.of(roleUser));

        return userRepository.save(user);
    }

    public User authenticate(AuthUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword())
        );

        return userRepository.findByUsername(input.getUsername())
                .orElseThrow();
    }
}
