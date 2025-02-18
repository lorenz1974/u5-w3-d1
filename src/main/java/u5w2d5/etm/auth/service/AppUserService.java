package u5w2d5.etm.auth.service;

import u5w2d5.etm.auth.JwtTokenUtil;
import u5w2d5.etm.auth.model.AppUser;
import u5w2d5.etm.auth.model.AppUserRole;
import u5w2d5.etm.auth.repository.AppUserRepository;
import u5w2d5.etm.auth.response.AppUserDetailsResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.beans.BeanUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*";
        StringBuilder password = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    public AppUser registerUser(String firstName, String lastName, String username, String email,
            String password,
            Set<AppUserRole> roles) {
        if (appUserRepository.existsByUsernameOrEmail(username, email)) {
            throw new EntityExistsException("Username or email already exists");
        }

        // Se alcuni parametri sono null o vuoti, li imposto con valori di default
        // Questo mi serve anche per il metodo registerUser(String, String, String) che
        // richiama questo metodo
        // e non ha bisogno di username, password o ruoli
        username = username == null || username.isEmpty() ? email.substring(0, email.indexOf('@')) : username;
        password = password == null || password.isEmpty() ? generateRandomPassword(20) : password;
        roles = (roles == null || roles.isEmpty()) ? Set.of(AppUserRole.ROLE_USER) : roles;

        AppUser appUser = new AppUser();
        appUser.setFirstName(firstName);
        appUser.setLastName(lastName);
        appUser.setUsername(username.toLowerCase());
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setEmail(email.toLowerCase());
        appUser.setRoles(roles);
        return appUserRepository.save(appUser);
    }

    // Metodo per la registrazione "veloce di un utente"
    // Modifico l'obbligatorietà dei campi nella Request
    public AppUser registerUser(String firstName, String lastName, String email) {
        AppUser registeredUser = registerUser(firstName, lastName, null,
                email, null, Set.of(AppUserRole.ROLE_USER));

        // Se l'utente non ha password, lo disabilito
        registeredUser.setEnabled(false);
        registeredUser.setCredentialsNonExpired(false);
        return appUserRepository.save(registeredUser);
    }

    public String authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtTokenUtil.generateToken(userDetails);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, appUser.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        appUser.setPassword(passwordEncoder.encode(newPassword));
        appUserRepository.save(appUser);
    }

    public void updateUserRoles(String username, Set<AppUserRole> roles) {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        appUser.setRoles(roles);
        appUserRepository.save(appUser);
    }

    public AppUserDetailsResponse getUser(String username, String email) {
        AppUser appUser = appUserRepository.findByUsernameOrEmail(username, email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        AppUserDetailsResponse appUserDetailsResponse = new AppUserDetailsResponse();
        BeanUtils.copyProperties(appUser, appUserDetailsResponse);
        return appUserDetailsResponse;
    }

    public AppUserDetailsResponse getCurrentUser() {
        // Estraggo il contesto di sicurezza locale
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Estraggo l'utente autenticato con il metodo standard
        AppUserDetailsResponse userDetailsResponse = getUser(authentication.getName(), authentication.getName());

        return userDetailsResponse;
    }
}
