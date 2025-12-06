package com.harshit.rideshare.service;

import com.harshit.rideshare.dto.AuthRequest;
import com.harshit.rideshare.dto.AuthResponse;
import com.harshit.rideshare.dto.RegisterRequest;
import com.harshit.rideshare.exception.BadRequestException;
import com.harshit.rideshare.model.User;
import com.harshit.rideshare.repository.UserRepository;
import com.harshit.rideshare.config.JwtUtil;
import com.harshit.rideshare.util.PasswordEncoderUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(PasswordEncoderUtil.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!PasswordEncoderUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }
}
