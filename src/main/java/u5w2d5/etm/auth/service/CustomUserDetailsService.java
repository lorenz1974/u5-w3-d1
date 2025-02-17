package u5w2d5.etm.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import u5w2d5.etm.auth.repository.*;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!appUserRepository.existsByUsernameOrEmail(username, username)) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return appUserRepository.findByUsernameOrEmail(username, username).get();
    }
}
