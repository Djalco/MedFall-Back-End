package fr._il.MedFall.services;

import fr._il.MedFall.DTO.ClassCountConditionDTO;

import java.util.List;

public interface ClassCountConditionService {

    // --- CRUD de base ---
    List<ClassCountConditionDTO> getAllClassCountConditions();
    ClassCountConditionDTO getClassCountConditionById(Long id);
    ClassCountConditionDTO createClassCountCondition(ClassCountConditionDTO dto);
    ClassCountConditionDTO updateClassCountCondition(Long id, ClassCountConditionDTO dto);
    void deleteClassCountCondition(Long id);

    // --- Recherches spécifiques ---
    List<ClassCountConditionDTO> getClassCountConditionsByRuleId(Long ruleId);
    ClassCountConditionDTO getConditionByMedicationClassId(Long classId);

    // --- Suppressions liées ---
    void deleteByMedicationClassId(Long classId);
    void deleteByRuleId(Long ruleId);
}
