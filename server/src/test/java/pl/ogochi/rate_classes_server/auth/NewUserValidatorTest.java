package pl.ogochi.rate_classes_server.auth;

import org.junit.Before;
import org.junit.Test;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class NewUserValidatorTest {

    private UserRepository userRepository;
    private User user;

    @Before
    public void setUp() throws Exception {
        userRepository = mock(UserRepository.class);
        user = new User("", "", new ArrayList<>());
    }

    @Test
    public void isCorrectEmailValid() {
        // given
        user.setEmail("abc.example@students.uw.edu.pl");
        NewUserValidator userValidator = new NewUserValidator(userRepository, user);

        // when
        boolean result = userValidator.isEmailValid();

        // then
        assertTrue(result);
    }

    @Test
    public void isEmailWithForbiddenCharactersNotValid() {
        // given
        user.setEmail("abc.e\\xam\"ple@students.uw.edu.pl");
        NewUserValidator userValidator = new NewUserValidator(userRepository, user);

        // when
        boolean result = userValidator.isEmailValid();

        // then
        assertFalse(result);
    }

    @Test
    public void isEmailWithoutAtCharacterNotValid() {
        // given
        user.setEmail("abc.examplestudents.uw.edu.pl");
        NewUserValidator userValidator = new NewUserValidator(userRepository, user);

        // when
        boolean result = userValidator.isEmailValid();

        // then
        assertFalse(result);
    }

    @Test
    public void isShortPasswordNotValid() {
        // given
        user.setPassword("aaa");
        NewUserValidator userValidator = new NewUserValidator(userRepository, user);

        // when
        boolean result = userValidator.isPasswordValid();

        // then
        assertFalse(result);
    }

    @Test
    public void emailNotUnique() {
        // given
        given(userRepository.findUserByEmail(user.getEmail()))
                .willReturn(Optional.of(new User()));
        NewUserValidator userValidator = new NewUserValidator(userRepository, user);

        // when
        boolean result = userValidator.isEmailUnique();

        // then
        assertFalse(result);
    }
}