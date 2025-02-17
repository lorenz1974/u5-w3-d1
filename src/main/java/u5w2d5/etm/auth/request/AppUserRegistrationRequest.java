package u5w2d5.etm.auth.request;

import lombok.Data;

@Data
public class AppUserRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String role;
}
