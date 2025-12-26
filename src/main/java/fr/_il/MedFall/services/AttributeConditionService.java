package fr._il.MedFall.services;

import fr._il.MedFall.DTO.AttributeConditionDTO;
import fr._il.MedFall.enums.Scope;

import java.util.List;

public interface AttributeConditionService {
    List<AttributeConditionDTO> getAllAttributeConditions();
    AttributeConditionDTO getAttributeConditionById(Long id);
    AttributeConditionDTO createAttributeCondition(AttributeConditionDTO attributeConditionDTO);
    AttributeConditionDTO updateAttributeCondition(Long id, AttributeConditionDTO attributeConditionDTO);
    void deleteAttributeCondition(Long id);
    List<AttributeConditionDTO> searchAttributeConditions(String attribute);
    List<AttributeConditionDTO> getAttributeConditionsByScope(Scope scope);

    // Méthodes pour les relations avec Rule
    List<AttributeConditionDTO> getAttributeConditionsByRule(Long ruleId);
    AttributeConditionDTO assignRuleToAttributeCondition(Long attributeConditionId, Long ruleId);
    AttributeConditionDTO removeRuleFromAttributeCondition(Long attributeConditionId);

    // Méthodes pour les relations avec Molecule
    List<AttributeConditionDTO> getAttributeConditionsByMolecule(Long moleculeId);
    AttributeConditionDTO assignMoleculeToAttributeCondition(Long attributeConditionId, Long moleculeId);
    AttributeConditionDTO removeMoleculeFromAttributeCondition(Long attributeConditionId);

    // Méthodes pour les statistiques
    Long countAttributeConditionsByRule(Long ruleId);
}