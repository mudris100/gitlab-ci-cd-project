package dev.udris.oauth2;

import dev.udris.entity.User;
import dev.udris.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByProviderAndUsername("local", username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return CustomUserPrincipal.fromUser(user);
    }

}
