package pl.ogochi.rate_classes_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.ogochi.rate_classes_server.model.RoleName;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.payload.LoginRegisterRequest;
import pl.ogochi.rate_classes_server.repository.UserRepository;
import pl.ogochi.rate_classes_server.security.JwtTokenProvider;
import pl.ogochi.rate_classes_server.util.NewUserValidator;

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

        NewUserValidator validator = new NewUserValidator(user);

        if (!validator.isEmailUnique() || !validator.isEmailValid() || !validator.isPasswordValid()) {
            validationConflict();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    private User createNewUser(LoginRegisterRequest request) {
        return new User(request.getEmail(), passwordEncoder.encode(request.getPassword()), false,
                Arrays.asList(RoleName.ROLE_USER.name()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private void validationConflict() {}
}
