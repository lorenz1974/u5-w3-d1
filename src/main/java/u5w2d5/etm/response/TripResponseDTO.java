package u5w2d5.etm.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import u5w2d5.etm.model.TripStatus;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripResponseDTO {

    private Long id;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private TripStatus status;

    private List<Long> employeeIds;
}
