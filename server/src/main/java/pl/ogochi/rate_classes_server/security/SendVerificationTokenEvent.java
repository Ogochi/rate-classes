package pl.ogochi.rate_classes_server.security;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;
import pl.ogochi.rate_classes_server.model.User;

@Data
@EqualsAndHashCode(callSuper=false)
public class SendVerificationTokenEvent extends ApplicationEvent {
    private User user;
    private String token;

    public SendVerificationTokenEvent(User user, String token) {
        super(token);
        this.user = user;
        this.token = token;
    }
}
