package pl.ogochi.rate_classes_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Provided email or password is not valid")
public class UserValidationException extends RuntimeException {
}
