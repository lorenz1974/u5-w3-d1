package u5w2d5.etm.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import u5w2d5.etm.auth.AppUser;
import u5w2d5.etm.auth.AppUserService;
import u5w2d5.etm.auth.Role;

import java.util.Optional;
import java.util.Set;

@Order(2)
@Component
@RequiredArgsConstructor
public class AuthRunner implements ApplicationRunner {

    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Creazione dell'utente admin se non esiste
        Optional<AppUser> adminUser = appUserService.findByUsername("admin");
        if (adminUser.isEmpty()) {
            appUserService.registerUser("admin", "adminpwd", Set.of(Role.ROLE_ADMIN));
        }

        // Creazione dell'utente user se non esiste
        Optional<AppUser> normalUser = appUserService.findByUsername("user");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user", "userpwd", Set.of(Role.ROLE_USER));
        }

        // Creazione dell'utente seller se non esiste
        Optional<AppUser> normalSeller = appUserService.findByUsername("seller");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("seller", "sellerpwd", Set.of(Role.ROLE_SELLER));
        }

    }
}
