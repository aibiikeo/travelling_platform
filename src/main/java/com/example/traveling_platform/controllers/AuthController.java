package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dto.JwtResponse;
import com.example.traveling_platform.dto.SignupDto;
import com.example.traveling_platform.dto.UserDto;
import com.example.traveling_platform.entities.UserEntity;
import com.example.traveling_platform.repositories.UserRepository;
import com.example.traveling_platform.security.JwtTokenUtil;
import com.example.traveling_platform.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trusted/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use");
        }
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignupDto dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenUtil.generateToken(authentication.getName());

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }

    @GetMapping("/user/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
