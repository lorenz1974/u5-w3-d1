// src/main/java/u5w2d5/etm/model/Employee.java
package u5w2d5.etm.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
@JsonIgnoreProperties({ "bookings" })
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(unique = true, nullable = false)
    protected String username;

    @Column(nullable = false)
    protected String firstName;

    @Column(nullable = false)
    protected String lastName;

    protected String email;

    protected String avatarUrl;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    // vado in override in modo da accertarmi che username e email siano sempre in
    // minuscolo
    public void setUsername(String username) {
        // Rendo lo username minuscolo e rimuovo eventuali spazi o apici
        this.username = username.toLowerCase().replace(" ", "").replace("'", "");
    }

    // vado in override in modo da accertarmi che username e email siano sempre in
    // minuscolo
    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

}
