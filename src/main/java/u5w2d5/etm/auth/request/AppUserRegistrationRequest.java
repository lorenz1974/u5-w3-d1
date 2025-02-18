package u5w2d5.etm.auth.request;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@Validated
public class AppUserRegistrationRequest {
    @NotNull
    @Size(min = 2, max = 50)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 50)
    private String lastName;

    @NotNull
    @Size(min = 2, max = 50)
    private String email;

    @Size(min = 2, max = 50)
    private String username;

    @Size(min = 5, max = 16)
    private String password;

    // Questo può essere nullo perché il service lo setta a ROLE_USER di default
    private String role;
}
