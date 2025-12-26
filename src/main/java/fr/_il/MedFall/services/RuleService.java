package fr._il.MedFall.services;

import fr._il.MedFall.DTO.RuleDTO;
import fr._il.MedFall.enums.ConditionStrategy;

import java.util.List;

public interface RuleService {
    List<RuleDTO> getAllRules();
    RuleDTO getRuleById(Long id);
    RuleDTO getRuleByLabel(String label);
    RuleDTO createRule(RuleDTO ruleDTO);
    RuleDTO updateRule(Long id, RuleDTO ruleDTO);
    void deleteRule(Long id);
    List<RuleDTO> searchRules(String label);
    List<RuleDTO> getRulesByStrategy(ConditionStrategy strategy);

    // Méthodes pour la gestion des relations avec Pathology
    List<RuleDTO> getRulesByPathology(Long pathologyId);
    RuleDTO assignPathologyToRule(Long ruleId, Long pathologyId);
    RuleDTO removePathologyFromRule(Long ruleId);

    // Méthodes pour les statistiques
    Long countRulesByPathology(Long pathologyId);
}