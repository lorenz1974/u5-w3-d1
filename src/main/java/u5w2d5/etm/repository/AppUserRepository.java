package u5w2d5.etm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import u5w2d5.etm.auth.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}
