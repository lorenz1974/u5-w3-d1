package u5w2d5.etm.auth;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import u5w2d5.etm.auth.model.*;
import u5w2d5.etm.auth.request.*;
import u5w2d5.etm.auth.response.*;
import u5w2d5.etm.auth.service.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public idResponse register(@RequestBody AppUserRegistrationRequest registerRequest) {
        AppUser appUser = appUserService.registerUser(
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                Set.of(registerRequest.getRole() == null ? AppUserRole.ROLE_USER
                        : AppUserRole.valueOf("ROLE_" + registerRequest.getRole())));
        return new idResponse(appUser.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<AppUserAuthResponse> login(@RequestBody AppUserLoginRequest loginRequest) {
        String token = appUserService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword());
        return ResponseEntity.ok(new AppUserAuthResponse(token));
    }
}