package u5w2d5.etm.auth.request;

import lombok.Data;

@Data
public class AppUserLoginRequest {
    private String username;
    private String password;
}
