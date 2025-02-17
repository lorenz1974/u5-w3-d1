
package u5w2d5.etm.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {

    @NotNull(message = "Viaggio è obbligatorio")
    @Min(value = 1, message = "Viaggio deve essere un numero positivo")
    private Long tripId;

    @NotNull(message = "Dipendente è obbligatorio")
    @Min(value = 1, message = "Dipendente deve essere un numero positivo")
    private Long employeeId;

    @NotNull
    private LocalDateTime requestDate = LocalDateTime.now();

    private String notes;
}
