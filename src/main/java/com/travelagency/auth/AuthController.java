package com.travelagency.auth;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AccountValid authService = new AccountValid();

    @PostMapping("/register")
    public AccountValid.RegistrationResult register(@RequestBody Map<String, String> data) throws Exception {
        return authService.registerUser(
                data.get("email"),
                data.get("password"),
                data.get("fullName"),
                data.get("phoneNumber")
        );
    }

    @PostMapping("/login")
    public AccountValid.LoginResult login(@RequestBody Map<String, String> data) throws Exception {
        return authService.loginUser(
                data.get("email"),
                data.get("password")
        );
    }
}