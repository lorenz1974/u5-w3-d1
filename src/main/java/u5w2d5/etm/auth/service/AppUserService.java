package u5w2d5.etm.auth.service;

import u5w2d5.etm.auth.JwtTokenUtil;
import u5w2d5.etm.auth.model.AppUser;
import u5w2d5.etm.auth.model.AppUserRole;
import u5w2d5.etm.auth.repository.AppUserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public AppUser registerUser(String firstName, String lastName, String username, String email, String password,
            Set<AppUserRole> roles) {
        if (appUserRepository.existsByUsernameOrEmail(username, email)) {
            throw new EntityExistsException("Username or email already exists");
        }

        AppUser appUser = new AppUser();

        log.debug("Registering user...");
        log.debug(" - firstName: {}", firstName);
        log.debug(" - lastName: {}", lastName);
        log.debug(" - username: {}", username);
        log.debug(" - email: {}", email);
        log.debug(" - password: {}", password);
        log.debug(" - roles: {}", roles);

        appUser.setFirstName(firstName);
        appUser.setLastName(lastName);
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setEmail(email);
        appUser.setRoles(roles);
        return appUserRepository.save(appUser);
    }

    public String authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtTokenUtil.generateToken(userDetails);
    }
}