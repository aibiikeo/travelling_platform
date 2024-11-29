package com.example.traveling_platform.controllers;

import com.example.traveling_platform.dto.JwtResponse;
import com.example.traveling_platform.dto.SignupDto;
import com.example.traveling_platform.dto.UserDto;
import com.example.traveling_platform.entities.UserEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.repositories.UserRepository;
import com.example.traveling_platform.security.JwtTokenUtil;
import com.example.traveling_platform.services.EmailService;
import com.example.traveling_platform.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/trusted/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
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

    @Autowired
    private EmailService emailService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            System.out.println("Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use");
        }
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        String token = jwtTokenUtil.generateVerificationToken(dto.getEmail());
        emailService.sendVerificationEmail(dto.getEmail(), token);

        return ResponseEntity.ok("User registered successfully. verify your email");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignupDto dto) {
        UserEntity user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isVerified()) {
            return ResponseEntity.status(HttpStatusCode.valueOf(401))
                    .body("Verify your email before logging in");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenUtil.generateToken(authentication.getName());

            // Return token and email
            return ResponseEntity.ok(new JwtResponse(token, user.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }


    @GetMapping("/user/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }


    @GetMapping("/user/id-by-email")
    public ResponseEntity<?> getUserIdByEmail(@RequestParam String email) {
        log.info("Fetching user ID for email: {}", email);
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.warn("User not found for email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        UserEntity user = userOptional.get();
        log.info("Found user ID: {}", user.getId());
        return ResponseEntity.ok(user.getId());
    }


    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            log.info("Received Token: {}", token);
            String email = jwtTokenUtil.getEmailFromToken(token);

            if (email == null) {
                return ResponseEntity.status(HttpStatusCode.valueOf(400)).body("email is null");
            }

            UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException("verification token has expired or wrong email", HttpStatusCode.valueOf(404)));
            if (user.isVerified()) {
                return ResponseEntity.status(HttpStatusCode.valueOf(400)).body("you already verified your email");
            }
            user.setVerified(true);
            userRepository.save(user);
            return ResponseEntity.ok("Email verified successfully");

        } catch (ExpiredJwtException e) {
            log.error("your token is expired", e);
            UserEntity user = userRepository.findByEmail(jwtTokenUtil.getEmailFromExpiredToken(token)).orElseThrow(() -> new ApiException("couldn't find email", HttpStatusCode.valueOf(404)));
            userRepository.delete(user);
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).body("your token is expired. sign up again and verify email");
        }
        catch (Exception e) {
            log.error("error while verifying email", e);
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).body("Invalid or expired token");
        }
    }
}
