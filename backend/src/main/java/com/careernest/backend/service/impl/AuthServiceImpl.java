package com.careernest.backend.service.impl;

import com.careernest.backend.dto.request.LoginRequest;
import com.careernest.backend.dto.request.RegisterRequest;
import com.careernest.backend.dto.response.AuthResponse;
import com.careernest.backend.exception.EmailAlreadyExistsException;
import com.careernest.backend.model.User;
import com.careernest.backend.repository.UserRepository;
import com.careernest.backend.security.JwtUtil;
import com.careernest.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        User user = userRepository.save(User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .build());

        return buildResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException (-> 401) when email/password don't match.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getEmail()));

        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getEmail()))
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
