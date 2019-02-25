package pl.ogochi.rate_classes_server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class RegistrationListener {
    private static final String VERIFY_EMAIL_TOPIC = "Email verification";

    @Autowired
    public JavaMailSender emailSender;

    @Value("${spring.application.name}")
    private String appName;
    @Value("${spring.application.url}")
    private String appUrl;

    @EventListener
    public void onApplicationEvent(SendVerificationTokenEvent event) {
        try {
            sendVerificationToken(event);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void sendVerificationToken(SendVerificationTokenEvent event) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

        helper.setText(createHtmlMessage(event.getToken()),true);
        helper.setTo(event.getUser().getEmail());
        helper.setSubject(appName + " - " + VERIFY_EMAIL_TOPIC);

        emailSender.send(message);
    }

    private String createHtmlMessage(String token) {
        return String.format("<h1>Dear %s user!</h1><br/>" +
                "Verify your email address by clicking <a href='%s'>this link</a>.",
                appName, appUrl + "/api/auth/verify?token=" + token);
    }
}
