package pl.ogochi.rate_classes_server.util;

import org.springframework.beans.factory.annotation.Autowired;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.repository.UserRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUserValidator {
    private UserRepository userRepository;
    private User user;

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public NewUserValidator(UserRepository userRepository, User user) {
        this.userRepository = userRepository;
        this.user = user;
    }

    public boolean isEmailValid() {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(user.getEmail());
        return matcher.find();
    }

    public boolean isPasswordValid() {
        return user.getPassword().length() >= 8;
    }

    public boolean isEmailUnique() {
        return !userRepository.findUserByEmail(user.getEmail()).isPresent();
    }
}
