package pl.ogochi.rate_classes_server.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

@Data
@EqualsAndHashCode(callSuper=false)
class ResetPasswordEvent extends ApplicationEvent {
    private String email;

    public ResetPasswordEvent(String email) {
        super(email);
        this.email = email;
    }
}
