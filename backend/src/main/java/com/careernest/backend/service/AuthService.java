package com.careernest.backend.service;

import com.careernest.backend.dto.request.LoginRequest;
import com.careernest.backend.dto.request.RegisterRequest;
import com.careernest.backend.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
