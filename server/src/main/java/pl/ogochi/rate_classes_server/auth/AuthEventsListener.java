package pl.ogochi.rate_classes_server.auth;

import lombok.extern.slf4j.Slf4j;
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
    private static final String RESET_PASSWORD_EMAIL_TOPIC = "Password reset";

    @Autowired
    public JavaMailSender emailSender;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public PasswordEncoder passwordEncoder;

    @Value("${spring.application.name}")
    private String appName;
    @Value("${app.redirect.url.default}")
    private String appUrl;

    @EventListener
    public void onApplicationEvent(SendVerificationTokenEvent event) {
        try {
            sendEmail(event.getUser().getEmail(), getMessageForVerification(event.getToken()), VERIFY_EMAIL_TOPIC);
        } catch (MessagingException e) {
            log.error("Error during email message creation with stack trace={}", e.getMessage());
        }
    }

    @EventListener
    @Transactional
    public void onApplicationEvent(ResetPasswordEvent event) {
        String newPassword = UUID.randomUUID().toString().substring(0, 8);

        Optional<User> maybeUser = userRepository.findUserByEmail(event.getEmail());
        if (!maybeUser.isPresent()) {
            log.warn("User with email={} not found during password reset", event.getEmail());
            return;
        }

        User user = maybeUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        try {
            sendEmail(event.getEmail(), getMessageForPasswordReset(newPassword), RESET_PASSWORD_EMAIL_TOPIC);
        } catch (MessagingException e) {
            log.error("Error during email message creation with stack trace={}", e.getMessage());
        }

    }

    private void sendEmail(String email, String messageText, String subject) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

        helper.setText(messageText,true);
        helper.setTo(email);
        helper.setSubject(appName + " - " + subject);

        emailSender.send(message);
    }

    private String getMessageForPasswordReset(String newPassword) {
        return String.format("<h3>Dear <b>%s</b> user!</h3><br/>" +
                        "We have reset your current password. Please log in as soon as possible and change password.<br/> +" +
                        "Password: <b>%s</b>",
                appName, newPassword);
    }

    private String getMessageForVerification(String token) {
        return String.format("<h3>Dear <b>%s</b> user!</h3><br/>" +
                "Verify your email address by clicking <a href='%s'>this link</a>.",
                appName, appUrl + "/api/auth/verify?token=" + token);
    }
}
