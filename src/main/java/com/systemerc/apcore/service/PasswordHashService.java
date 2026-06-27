package com.systemerc.apcore.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordHashService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String hash(String plainPassword) {
        return encoder.encode(plainPassword);
    }
}
