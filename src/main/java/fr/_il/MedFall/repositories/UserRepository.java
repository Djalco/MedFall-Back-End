package fr._il.MedFall.repositories;

import fr._il.MedFall.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmailEquals(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNbr);
}
