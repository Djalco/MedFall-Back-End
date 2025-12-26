package fr._il.MedFall.servicesImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr._il.MedFall.DTO.ClassCountConditionDTO;
import fr._il.MedFall.entities.ClassCountCondition;
import fr._il.MedFall.entities.MedicationClass;
import fr._il.MedFall.entities.Rule;
import fr._il.MedFall.repositories.ClassCountConditionRepository;
import fr._il.MedFall.repositories.MedicationClassRepository;
import fr._il.MedFall.repositories.RuleRepository;
import fr._il.MedFall.services.ClassCountConditionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassCountConditionServiceImpl implements ClassCountConditionService {

    private final ClassCountConditionRepository classCountConditionRepository;
    private final RuleRepository ruleRepository;
    private final MedicationClassRepository medicationClassRepository;

    // =========================================================
    // CRUD
    // =========================================================

    @Transactional(readOnly = true)
    @Override
    public List<ClassCountConditionDTO> getAllClassCountConditions() {
        return classCountConditionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ClassCountConditionDTO getClassCountConditionById(Long id) {
        ClassCountCondition entity = classCountConditionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClassCountCondition with ID " + id + " not found"));
        return toDto(entity);
    }

    @Override
public ClassCountConditionDTO createClassCountCondition(ClassCountConditionDTO dto) {
    // Vérifie qu'une condition n'existe pas déjà pour la combinaison Rule + MedicationClass
    if (dto.getRuleId() != null && dto.getMedicationClassId() != null) {
        if (classCountConditionRepository.existsByRuleIdAndMedicationClass_Id(dto.getRuleId(), dto.getMedicationClassId())) {
            throw new IllegalArgumentException("Une condition existe déjà pour cette Rule et cette MedicationClass");
        }
    }

    ClassCountCondition entity = toEntity(dto);
    entity.setComparator(dto.getComparator());
    entity.setThreshold(dto.getThreshold());
    entity.setDistinctMolecules(dto.getDistinctMolecules() != null ? dto.getDistinctMolecules() : false);

    if (dto.getRuleId() != null) {
        entity.setRule(ruleRepository.getReferenceById(dto.getRuleId()));
    }

    if (dto.getMedicationClassId() != null) {
        entity.setMedicationClass(medicationClassRepository.getReferenceById(dto.getMedicationClassId()));
    }

    entity = classCountConditionRepository.save(entity);

    ClassCountConditionDTO result = new ClassCountConditionDTO();
    result.setId(entity.getId());
    result.setComparator(entity.getComparator());
    result.setThreshold(entity.getThreshold());
    result.setDistinctMolecules(entity.getDistinctMolecules());
    result.setRuleId(entity.getRule() != null ? entity.getRule().getId() : null);
    result.setMedicationClassId(entity.getMedicationClass() != null ? entity.getMedicationClass().getId() : null);

    return result;
}



    @Override
    public ClassCountConditionDTO updateClassCountCondition(Long id, ClassCountConditionDTO dto) {
        ClassCountCondition existingEntity = classCountConditionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClassCountCondition with ID " + id + " not found for update"));

        // Mise à jour des champs de base
        existingEntity.setComparator(dto.getComparator());
        existingEntity.setThreshold(dto.getThreshold());
        existingEntity.setDistinctMolecules(dto.getDistinctMolecules());

        // Mise à jour de la relation Rule si nécessaire
        if (dto.getRuleId() != null && (existingEntity.getRule() == null || !dto.getRuleId().equals(existingEntity.getRule().getId()))) {
            Rule newRule = ruleRepository.findById(dto.getRuleId())
                    .orElseThrow(() -> new RuntimeException("Rule with ID " + dto.getRuleId() + " not found"));
            existingEntity.setRule(newRule);
        }

        // Mise à jour de la relation MedicationClass si nécessaire
        if (dto.getMedicationClassId() != null && (existingEntity.getMedicationClass() == null || !dto.getMedicationClassId().equals(existingEntity.getMedicationClass().getId()))) {
            MedicationClass newClass = medicationClassRepository.findById(dto.getMedicationClassId())
                    .orElseThrow(() -> new RuntimeException("MedicationClass with ID " + dto.getMedicationClassId() + " not found"));
            existingEntity.setMedicationClass(newClass);
        }

        ClassCountCondition updatedEntity = classCountConditionRepository.save(existingEntity);
        return toDto(updatedEntity);
    }

    @Override
    public void deleteClassCountCondition(Long id) {
        if (!classCountConditionRepository.existsById(id)) {
            throw new RuntimeException("ClassCountCondition with ID " + id + " not found for deletion");
        }
        classCountConditionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassCountConditionDTO> getClassCountConditionsByRuleId(Long ruleId) {
        return classCountConditionRepository.findByRuleId(ruleId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClassCountConditionDTO getConditionByMedicationClassId(Long classId) {
        ClassCountCondition entity = classCountConditionRepository.findByMedicationClass_Id(classId)
                .orElseThrow(() -> new RuntimeException(
                        "ClassCountCondition with MedicationClass ID " + classId + " not found"));
        return toDto(entity);
    }

    @Override
    public void deleteByMedicationClassId(Long classId) {
        classCountConditionRepository.deleteByMedicationClass_Id(classId);
    }

    @Override
    public void deleteByRuleId(Long ruleId) {
        classCountConditionRepository.deleteByRuleId(ruleId);
    }



    // =========================================================
    // MAPPING MANUEL (DTO <-> ENTITY)
    // =========================================================

    private ClassCountConditionDTO toDto(ClassCountCondition entity) {
        if (entity == null) return null;

        return ClassCountConditionDTO.builder()
                .id(entity.getId())
                .comparator(entity.getComparator())
                .threshold(entity.getThreshold())
                .distinctMolecules(entity.getDistinctMolecules())
                .ruleId(entity.getRule() != null ? entity.getRule().getId() : null)
                .ruleLabel(entity.getRule() != null ? entity.getRule().getLabel() : null)
                .medicationClassId(entity.getMedicationClass() != null ? entity.getMedicationClass().getId() : null)
                .medicationClassName(entity.getMedicationClass() != null ? entity.getMedicationClass().getName() : null)
                .build();
    }

    private ClassCountCondition toEntity(ClassCountConditionDTO dto) {
        if (dto == null) return null;

        Rule rule = ruleRepository.findById(dto.getRuleId())
                .orElseThrow(() -> new RuntimeException("Rule with ID " + dto.getRuleId() + " not found"));

        MedicationClass medicationClass = medicationClassRepository.findById(dto.getMedicationClassId())
                .orElseThrow(() -> new RuntimeException("MedicationClass with ID " + dto.getMedicationClassId() + " not found"));

        return ClassCountCondition.builder()
                .id(dto.getId())
                .comparator(dto.getComparator())
                .threshold(dto.getThreshold())
                .distinctMolecules(dto.getDistinctMolecules())
                .rule(rule)
                .medicationClass(medicationClass)
                .build();
    }
}
