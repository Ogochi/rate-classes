package pl.ogochi.rate_classes_server.security;

import org.springframework.context.ApplicationListener;

public class RegistrationListener implements ApplicationListener<SendVerificationTokenEvent> {
    @Override
    public void onApplicationEvent(SendVerificationTokenEvent event) {
        sendVerificationToken(event);
    }

    private void sendVerificationToken(SendVerificationTokenEvent event) {

    }
}
