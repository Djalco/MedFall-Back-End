package fr._il.MedFall.repositories;

import fr._il.MedFall.entities.Pathology;
import fr._il.MedFall.entities.Target;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PathologyRepository extends JpaRepository<Pathology, Long> {
    List<Pathology> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String namePathology);
    Optional<Pathology> findByName(String namePathology);

    // Nouvelles méthodes pour les relations avec Target
    List<Pathology> findByTarget(Target target);
    List<Pathology> findByTargetIsNull();

    @Query("SELECT p FROM Pathology p WHERE p.target.id = :targetId")
    List<Pathology> findByTargetId(@Param("targetId") Long targetId);

    @Query("SELECT COUNT(p) FROM Pathology p WHERE p.target.id = :targetId")
    long countByTargetId(@Param("targetId") Long targetId);
}