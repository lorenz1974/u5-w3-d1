package u5w2d5.etm.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {

    private Long id;

    private Long tripId;

    private Long employeeId;

    private LocalDateTime requestDate = LocalDateTime.now();

    private String notes;
}
