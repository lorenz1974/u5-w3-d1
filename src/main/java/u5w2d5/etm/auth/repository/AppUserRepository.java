package u5w2d5.etm.auth.repository;

import u5w2d5.etm.auth.model.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    boolean existsByEmail(String email);

    Optional<AppUser> findByEmail(String email);

    boolean existsByUsername(String username);

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    Optional<AppUser> findByUsernameOrEmail(String username, String email);

}