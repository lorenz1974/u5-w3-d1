// src/main/java/u5w2d5/etm/model/Employee.java
package u5w2d5.etm.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDTO {

    @NotNull(message = "Username è obbligatorio")
    @Size(min = 2, max = 50, message = "Username deve essere tra 10 e 50 caratteri")
    @Column(unique = true)
    private String username;

    @NotNull(message = "Nome è obbligatorio")
    @Size(min = 2, max = 50, message = "Nome deve essere tra 10 e 50 caratteri")
    private String firstName;

    @NotNull(message = "Cognome è obbligatorio")
    @Size(min = 2, max = 50, message = "Cognome deve essere tra 10 e 50 caratteri")
    private String lastName;

    @Email(message = "Email non valida")
    @NotBlank(message = "Email è obbligatoria")
    private String email;

    private String avatarUrl;

}
