package pl.ogochi.rate_classes_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.ogochi.rate_classes_server.dao.LoginRegisterRequest;
import pl.ogochi.rate_classes_server.exception.UserValidationException;
import pl.ogochi.rate_classes_server.model.RoleName;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.model.VerificationToken;
import pl.ogochi.rate_classes_server.repository.UserRepository;
import pl.ogochi.rate_classes_server.repository.VerificationTokenRepository;
import pl.ogochi.rate_classes_server.security.JwtTokenProvider;
import pl.ogochi.rate_classes_server.security.SendVerificationTokenEvent;
import pl.ogochi.rate_classes_server.util.NewUserValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider tokenProvider;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    VerificationTokenRepository verificationTokenRepository;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/login")
    public String authenticate(@Valid @RequestBody LoginRegisterRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "Bearer " + tokenProvider.generateToken(authentication);
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody LoginRegisterRequest registerRequest) {
        User user = new User(registerRequest.getEmail(), registerRequest.getPassword(), false,
                Arrays.asList(RoleName.ROLE_USER.name()));

        NewUserValidator validator = new NewUserValidator(userRepository, user);

        if (!validator.isEmailUnique() || !validator.isEmailValid() || !validator.isPasswordValid()) {
            throw new UserValidationException();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        VerificationToken verificationToken = verificationTokenRepository.createNewToken(user);
        applicationEventPublisher.publishEvent(new SendVerificationTokenEvent(
                user,
                verificationToken.getToken()
        ));
    }

    @GetMapping("/verify")
    public void verifyEmail(@RequestParam String token) {

    }
}
