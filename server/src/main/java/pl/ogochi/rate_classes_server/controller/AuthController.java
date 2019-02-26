package pl.ogochi.rate_classes_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.ogochi.rate_classes_server.auth.AuthManagementService;
import pl.ogochi.rate_classes_server.dto.ChangePasswordRequest;
import pl.ogochi.rate_classes_server.dto.LoginRegisterRequest;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    AuthManagementService authManagementService;

    @Value("${app.redirect.url.default}")
    private String redirectUrl;

    @PostMapping("/register")
    @Transactional
    public void register(@Valid @RequestBody LoginRegisterRequest registerRequest) {
        authManagementService.registerUser(registerRequest);
    }

    @PostMapping("/login")
    @Transactional
    public String authenticate(@Valid @RequestBody LoginRegisterRequest loginRequest) {
        return authManagementService.authenticateUser(loginRequest);
    }

    @GetMapping("/verify")
    @Transactional
    public void verifyEmail(@RequestParam String emailVerificationToken, HttpServletResponse response) throws IOException {
        authManagementService.verifyEmail(emailVerificationToken);
        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/resendVerification")
    @Transactional
    public void resendVerificationEmail(@RequestParam String email) {
        authManagementService.resendVerificationEmail(email);
    }

    @PostMapping("/resetPassword")
    public void resetPassword(@RequestParam String email) {
        authManagementService.resetPassword(email);
    }

    @PostMapping("/changePassword")
    @RolesAllowed("ROLE_USER")
    @Transactional
    public void changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        authManagementService.changeUserPassword(changePasswordRequest);
    }
}