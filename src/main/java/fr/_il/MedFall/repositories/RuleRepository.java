package fr._il.MedFall.repositories;

import fr._il.MedFall.entities.Rule;
import fr._il.MedFall.enums.ConditionStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
    List<Rule> findByLabelContainingIgnoreCase(String label);
    boolean existsByLabel(String label);
    Optional<Rule> findByLabel(String label);
    List<Rule> findByStrategy(ConditionStrategy strategy);

    // Méthodes pour les relations avec Pathology
    List<Rule> findByPathologyId(Long pathologyId);

    @Query("SELECT COUNT(r) FROM Rule r WHERE r.pathology.id = :pathologyId")
    long countByPathologyId(@Param("pathologyId") Long pathologyId);

    // Méthodes pour rechercher par stratégie et pathologie
    List<Rule> findByPathologyIdAndStrategy(Long pathologyId, ConditionStrategy strategy);
}