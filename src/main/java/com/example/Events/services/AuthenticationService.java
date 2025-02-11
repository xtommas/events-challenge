package com.example.Events.services;

import com.example.Events.dtos.AuthUserDto;
import com.example.Events.models.Role;
import com.example.Events.models.User;
import com.example.Events.repositories.RoleRepository;
import com.example.Events.repositories.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
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

    public void promoteUser(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Access denied: You must be an admin to promote a user");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean userIsAdmin = user.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (userIsAdmin) {
            throw new RuntimeException("User is already an admin");
        }

        Set<Role> roles = new HashSet<>(user.getRoles());
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ADMIN");
                    return roleRepository.save(newRole);  // Save the new role to the database
                });
        roles.add(adminRole);
        user.setRoles(roles);
        userRepository.save(user);
    }
}
