package fr._il.MedFall.repositories;

import fr._il.MedFall.entities.Target;
import fr._il.MedFall.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TargetRepository extends JpaRepository<Target, Long> {
    List<Target> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
    Optional<Target> findByName(String name);
    List<Target> findByType(TargetType type);
}