package tn.rapid_post.agence.sec.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.rapid_post.agence.sec.entity.AppUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
