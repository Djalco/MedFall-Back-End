package fr._il.MedFall.services;

import fr._il.MedFall.DTO.RiskEffectDTO;

import java.util.List;

public interface RiskEffectService {
    // CRUD de base
    List<RiskEffectDTO> getAllRiskEffects();
    RiskEffectDTO getRiskEffectById(Long id);
    RiskEffectDTO createRiskEffect(RiskEffectDTO riskEffectDTO);
    RiskEffectDTO updateRiskEffect(Long id, RiskEffectDTO riskEffectDTO);
    void deleteRiskEffect(Long id);

    // Méthodes liées à Rule
    List<RiskEffectDTO> getRiskEffectsByRuleId(Long ruleId);
    void deleteRiskEffectsByRuleId(Long ruleId);
}
