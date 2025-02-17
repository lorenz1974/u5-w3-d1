package u5w2d5.etm.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import u5w2d5.etm.model.TripStatus;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestDTO {

    @NotNull(message = "Descrizione è obbligatoria")
    @Size(min = 10, max = 50, message = "Descrizione deve essere tra 10 e 50 caratteri")
    private String description;

    @NotNull(message = "Data di inizio è obbligatoria")
    private LocalDate startDate;

    @NotNull(message = "Data di fine è obbligatoria")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TripStatus status = TripStatus.SCHEDULED;
}
