package pl.ogochi.rate_classes_server.auth;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.repository.UserRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class AuthEventsListener {
    private static final String VERIFY_EMAIL_TOPIC = "Email verification";

    @Autowired
    public JavaMailSender emailSender;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public PasswordEncoder passwordEncoder;

    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.url}")
    private String appUrl;

    @EventListener
    public void onApplicationEvent(SendVerificationTokenEvent event) {
        try {
            sendEmail(event.getUser().getEmail(), getMessageForVerification(event.getToken()));
        } catch (MessagingException e) {
            log.error("Error during email message creation with stack trace={}", e.getMessage());
        }
    }

    @EventListener
    @Transactional
    public void onApplicationEvent(ResetPasswordEvent event) {
        String newPassword = UUID.randomUUID().toString().substring(0, 9);

        Optional<User> maybeUser = userRepository.findUserByEmail(event.getEmail());
        if (!maybeUser.isPresent()) {
            log.warn("User with email={} not found during password reset", event.getEmail());
            return;
        }

        User user = maybeUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        try {
            sendEmail(event.getEmail(), getMessageForPasswordReset(newPassword));
        } catch (MessagingException e) {
            log.error("Error during email message creation with stack trace={}", e.getMessage());
        }

    }

    private String getMessageForPasswordReset(String newPassword) {
        return String.format("<h3>Dear <b>%s</b> user!</h3><br/>" +
                "We have reset your current password. Please login as soon as possible using this new password: %s",
                appName, newPassword);
    }

    private void sendEmail(String email, String messageText) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

        helper.setText(messageText,true);
        helper.setTo(email);
        helper.setSubject(appName + " - " + VERIFY_EMAIL_TOPIC);

        emailSender.send(message);
    }

    private String getMessageForVerification(String token) {
        return String.format("<h3>Dear <b>%s</b> user!</h3><br/>" +
                "Verify your email address by clicking <a href='%s'>this link</a>.",
                appName, appUrl + "/api/auth/verify?token=" + token);
    }
}
