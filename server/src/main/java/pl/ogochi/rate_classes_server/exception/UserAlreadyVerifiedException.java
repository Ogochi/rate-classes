package pl.ogochi.rate_classes_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User is already verified")
public class UserAlreadyVerifiedException extends RuntimeException {
}
