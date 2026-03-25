package com.brayanspv.auth.component.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PasswordHelper {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String createPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
