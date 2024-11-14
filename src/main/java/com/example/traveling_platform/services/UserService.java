package com.example.traveling_platform.services;

import com.example.traveling_platform.dto.SignupDto;
import com.example.traveling_platform.dto.UserDto;
import com.example.traveling_platform.entities.UserEntity;
import com.example.traveling_platform.exceptions.ApiException;
import com.example.traveling_platform.model.UserModel;
import com.example.traveling_platform.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found"));

        return new UserModel(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getCreatedAt()
        );
    }
    public UserDto getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }

    public void saveUser(SignupDto signUpDto) {
        log.info("Sign up user: {}", signUpDto.getEmail());
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(signUpDto.getEmail());
        userEntity.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        try {
            userRepository.save(userEntity);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException("User " + signUpDto.getEmail() + " already exists", HttpStatusCode.valueOf(409));
        } catch (Exception e) {
            log.error("Error", e);
            throw new ApiException("Error while creating user", HttpStatusCode.valueOf(400));
        }
    }
}
