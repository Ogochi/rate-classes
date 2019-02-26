package pl.ogochi.rate_classes_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User did not make enough opinions")
public class NotEnoughUserOpinionsException extends RuntimeException {
}
