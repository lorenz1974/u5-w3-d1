// src/main/java/u5w2d5/etm/model/Employee.java
package u5w2d5.etm.response;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String avatarUrl;

    private List<Long> tripIds;
}
