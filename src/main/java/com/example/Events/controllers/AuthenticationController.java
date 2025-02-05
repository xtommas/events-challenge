package com.example.Events.controllers;

import com.example.Events.dtos.AuthUserDto;
import com.example.Events.dtos.LoginResponse;
import com.example.Events.models.User;
import com.example.Events.services.AuthenticationService;
import com.example.Events.services.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserDetailsService userDetailsService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody AuthUserDto registerUserInput) {
        User newUser = authenticationService.register(registerUserInput);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody AuthUserDto loginUserInput) {
        User authenticatedUser = authenticationService.authenticate(loginUserInput);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getJwtExpiration());
        loginResponse.setRefresh_token(jwtService.generateRefreshToken(authenticatedUser));

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) final String authorizationHeader
    ) {
        String refreshToken = authorizationHeader.substring(7);

        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            String newJwtToken = jwtService.generateToken(userDetails);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(newJwtToken);
            loginResponse.setExpiresIn(jwtService.getJwtExpiration());
            loginResponse.setRefresh_token(refreshToken);

            return ResponseEntity.ok(loginResponse);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
