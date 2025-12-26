package fr._il.MedFall.repositories;

import fr._il.MedFall.entities.RiskEffect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskEffectRepository extends JpaRepository<RiskEffect, Long> {
    // Récupérer tous les effets de risque associés à une règle
    List<RiskEffect> findByRule_Id(Long ruleId);

    // Supprimer tous les effets associés à une règle
    void deleteByRule_Id(Long ruleId);
}
