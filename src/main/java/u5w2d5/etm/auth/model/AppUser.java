package u5w2d5.etm.auth.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import u5w2d5.etm.model.Employee;

import org.springframework.security.core.userdetails.UserDetails;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "employee_type")
public class AppUser extends Employee implements UserDetails {

    // Questa roba non serve essendo una tabella singola. Si usano le proprietà
    // della classe padre ovvero "employee"
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(nullable = false)
    // private Long id;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "password_updated_at", nullable = false)
    private LocalDateTime passwordUpdatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AppUserRole> roles;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    // Creo il setPassword perché questo mi permette di aggiornare la data di cambio
    public void setPassword(String password) {
        // Per ragioni di standard la password deve arrivare già criptata
        this.password = password;
        this.passwordUpdatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> (GrantedAuthority) () -> role.name())
                .collect(Collectors.toSet());
    }

    public AppUser(String firstName, String lastName, String username, String password, String email,
            Collection<? extends GrantedAuthority> authorities) {
        this(firstName, lastName, username, password, email, true, true, true, true, authorities);
    }

    public AppUser(String firstName, String lastName, String username, String password, String email, boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.roles = authorities.stream()
                .map(authority -> AppUserRole.valueOf(authority.getAuthority()))
                .collect(Collectors.toSet());
    }

}
