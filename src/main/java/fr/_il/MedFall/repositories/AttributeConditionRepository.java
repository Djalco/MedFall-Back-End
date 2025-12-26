package fr._il.MedFall.repositories;

import fr._il.MedFall.entities.AttributeCondition;
import fr._il.MedFall.enums.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeConditionRepository extends JpaRepository<AttributeCondition, Long> {
    List<AttributeCondition> findByAttributeContainingIgnoreCase(String attribute);
    List<AttributeCondition> findByScope(Scope scope);

    // Méthodes pour les relations avec Rule
    List<AttributeCondition> findByRuleId(Long ruleId);

    @Query("SELECT COUNT(ac) FROM AttributeCondition ac WHERE ac.rule.id = :ruleId")
    long countByRuleId(@Param("ruleId") Long ruleId);

    // Méthodes pour les relations avec Molecule
    List<AttributeCondition> findByMoleculeId(Long moleculeId);
    Optional<AttributeCondition> findByMoleculeIdAndAttribute(Long moleculeId, String attribute);

    // Recherche combinée
    List<AttributeCondition> findByRuleIdAndScope(Long ruleId, Scope scope);
}