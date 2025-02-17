package u5w2d5.etm.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionMessage {
    private String message;
    private String status;
    private Object error;
    private LocalDateTime timestamp = LocalDateTime.now();
}
