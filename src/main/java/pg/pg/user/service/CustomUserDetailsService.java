package pg.pg.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pg.pg.user.model.User;
import pg.pg.user.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        // Try to find user by username, email, mobile, or PG number
        Optional<User> userOpt = userRepository.findByUsername(loginId);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(loginId);
        }
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByMobileNumber(loginId);
        }
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByPgNumber(loginId);
        }

        User user = userOpt.orElseThrow(() ->
                new UsernameNotFoundException("User not found with: " + loginId));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
