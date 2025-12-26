package fr._il.MedFall.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr._il.MedFall.entities.ClassCountCondition;

public interface ClassCountConditionRepository extends JpaRepository<ClassCountCondition, Long> {
    List<ClassCountCondition> findByRuleId(Long ruleId);
    Optional<ClassCountCondition> findByMedicationClass_Id(Long medicationClassId);
    boolean existsByRuleIdAndMedicationClass_Id(Long ruleId, Long medicationClassId);
    void deleteByMedicationClass_Id(Long medicationClassId);
    void deleteByRuleId(Long ruleId);
    

}
