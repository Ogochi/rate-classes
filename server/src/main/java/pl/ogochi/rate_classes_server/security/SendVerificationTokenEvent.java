package pl.ogochi.rate_classes_server.security;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;
import pl.ogochi.rate_classes_server.model.User;

import java.util.Locale;

@Data
@EqualsAndHashCode(callSuper=false)
public class SendVerificationTokenEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public SendVerificationTokenEvent(String appUrl, Locale locale, User user) {
        super(user);
        this.appUrl = appUrl;
        this.locale = locale;
        this.user = user;
    }
}
