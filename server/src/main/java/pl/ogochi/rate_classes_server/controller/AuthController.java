package pl.ogochi.rate_classes_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.ogochi.rate_classes_server.dao.ChangePasswordRequest;
import pl.ogochi.rate_classes_server.dao.LoginRegisterRequest;
import pl.ogochi.rate_classes_server.exception.UserAlreadyVerifiedException;
import pl.ogochi.rate_classes_server.exception.UserNotFoundException;
import pl.ogochi.rate_classes_server.exception.UserValidationException;
import pl.ogochi.rate_classes_server.exception.VerificationTokenNotFoundException;
import pl.ogochi.rate_classes_server.model.RoleName;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.model.VerificationToken;
import pl.ogochi.rate_classes_server.repository.UserRepository;
import pl.ogochi.rate_classes_server.repository.VerificationTokenRepository;
import pl.ogochi.rate_classes_server.security.JwtTokenProvider;
import pl.ogochi.rate_classes_server.security.SendVerificationTokenEvent;
import pl.ogochi.rate_classes_server.security.UserPrincipal;
import pl.ogochi.rate_classes_server.util.NewUserValidator;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

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

    @Value("${spring.application.url}")
    private String appUrl;

    @PostMapping("/login")
    @Transactional
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

    @PostMapping("/changePassword")
    @RolesAllowed("ROLE_USER")
    @Transactional
    public void changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        UserPrincipal userPrincipal = (UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userPrincipal.getPassword().equals(passwordEncoder.encode(changePasswordRequest.getCurrentPassword()))) {
            throw new UserValidationException();
        }

        User user = userRepository.getUserByEmail(userPrincipal.getEmail());
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    @PostMapping("/register")
    @Transactional
    public void register(@Valid @RequestBody LoginRegisterRequest registerRequest) {
        User user = new User(registerRequest.getEmail(), registerRequest.getPassword(), false,
                Arrays.asList(RoleName.ROLE_USER.name()));

        NewUserValidator validator = new NewUserValidator(userRepository, user);

        if (!validator.isEmailUnique() || !validator.isEmailValid() || !validator.isPasswordValid()) {
            throw new UserValidationException();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        createAndSendVerificationTokenForUser(user);
    }

    @GetMapping("/verify")
    @Transactional
    public void verifyEmail(@RequestParam String token, HttpServletResponse response) throws IOException {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findById(token);

        if (!verificationToken.isPresent()) {
            throw new VerificationTokenNotFoundException();
        }

        User user = verificationToken.get().getUser();
        user.setEnabled(true);

        verificationTokenRepository.delete(verificationToken.get());
        userRepository.save(user);

        response.sendRedirect(appUrl);
    }

    @PostMapping("/resendVerification")
    @Transactional
    public void resendVerificationEmail(@RequestParam String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        } else if (user.get().isEnabled()) {
            throw new UserAlreadyVerifiedException();
        }

        verificationTokenRepository.removeAllByUser_Email(email);
        createAndSendVerificationTokenForUser(user.get());
    }

    private void createAndSendVerificationTokenForUser(User user) {
        VerificationToken verificationToken = verificationTokenRepository.createNewToken(user);
        applicationEventPublisher.publishEvent(new SendVerificationTokenEvent(
                user,
                verificationToken.getToken()
        ));
    }
}