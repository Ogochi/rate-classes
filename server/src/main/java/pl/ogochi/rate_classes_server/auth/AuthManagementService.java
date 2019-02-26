package pl.ogochi.rate_classes_server.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.ogochi.rate_classes_server.dto.ChangePasswordRequest;
import pl.ogochi.rate_classes_server.dto.LoginRegisterRequest;
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
import pl.ogochi.rate_classes_server.security.UserPrincipal;

import java.util.Arrays;
import java.util.Optional;

@Service
@Slf4j
public class AuthManagementService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String authenticateUser(LoginRegisterRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "Bearer " + tokenProvider.generateToken(authentication);
    }

    public void registerUser(LoginRegisterRequest registerRequest) {
        User user = new User(registerRequest.getEmail(), registerRequest.getPassword(),
                Arrays.asList(RoleName.ROLE_USER.name()));

        validateNewUser(user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        createAndSendVerificationTokenForUser(user);
    }

    public void verifyEmail(String emailVerificationToken) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findById(emailVerificationToken);

        if (!verificationToken.isPresent()) {
            throw new VerificationTokenNotFoundException();
        }

        User user = verificationToken.get().getUser();
        user.setEnabled(true);

        verificationTokenRepository.delete(verificationToken.get());
        userRepository.save(user);
    }

    public void resendVerificationEmail(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        } else if (user.get().isEnabled()) {
            throw new UserAlreadyVerifiedException();
        }

        verificationTokenRepository.removeAllByUser_Email(email);
        createAndSendVerificationTokenForUser(user.get());
    }

    public void resetPassword(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }

        applicationEventPublisher.publishEvent(new ResetPasswordEvent(user.get().getEmail()));
        log.info("Sent reset user password request for user with email={}", user.get().getEmail());
    }

    public void changeUserPassword(ChangePasswordRequest changePasswordRequest) {
        UserPrincipal userPrincipal = (UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), userPrincipal.getPassword())) {
            throw new UserValidationException("Password do not match current user password");
        }

        User user = userRepository.getUserByEmail(userPrincipal.getEmail());
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        log.info("Successfully changed password for user with email={}", userPrincipal.getEmail());
    }

    private void validateNewUser(User user) {
        NewUserValidator validator = new NewUserValidator(userRepository, user);

        if (!validator.isEmailUnique()) {
            throw new UserValidationException("Email is not unique");
        } else if (!validator.isEmailValid()) {
            throw new UserValidationException("Proposed email address is not valid email address");
        } else if (!validator.isPasswordValid()) {
            throw new UserValidationException("Password do not satisfy constraints");
        }
    }

    private void createAndSendVerificationTokenForUser(User user) {
        VerificationToken verificationToken = verificationTokenRepository.createNewToken(user);
        applicationEventPublisher.publishEvent(new SendVerificationTokenEvent(
                user,
                verificationToken.getToken()
        ));
    }
}
