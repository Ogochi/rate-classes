package pl.ogochi.rate_classes_server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.repository.UserRepository;

public class CustomUserDetails implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username)
                 .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " not found"));

        return UserPrincipal.create(user);
    }
}
