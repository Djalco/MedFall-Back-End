package fr._il.MedFall.servicesImpl;

import fr._il.MedFall.DTO.AttributeConditionDTO;
import fr._il.MedFall.entities.AttributeCondition;
import fr._il.MedFall.entities.Molecule;
import fr._il.MedFall.entities.Rule;
import fr._il.MedFall.enums.Scope;
import fr._il.MedFall.repositories.AttributeConditionRepository;
import fr._il.MedFall.repositories.MoleculeRepository;
import fr._il.MedFall.repositories.RuleRepository;
import fr._il.MedFall.services.AttributeConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AttributeConditionServiceImpl implements AttributeConditionService {

    private final AttributeConditionRepository attributeConditionRepository;
    private final RuleRepository ruleRepository;
    private final MoleculeRepository moleculeRepository;

    @Override
    public List<AttributeConditionDTO> getAllAttributeConditions() {
        return attributeConditionRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public AttributeConditionDTO getAttributeConditionById(Long id) {
        AttributeCondition attributeCondition = attributeConditionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AttributeCondition not found with id: " + id));
        return convertToDTO(attributeCondition);
    }

    @Override
    public AttributeConditionDTO createAttributeCondition(AttributeConditionDTO attributeConditionDTO) {
        AttributeCondition attributeCondition = convertToEntity(attributeConditionDTO);
        AttributeCondition savedAttributeCondition = attributeConditionRepository.save(attributeCondition);
        return convertToDTO(savedAttributeCondition);
    }

    @Override
    public AttributeConditionDTO updateAttributeCondition(Long id, AttributeConditionDTO attributeConditionDTO) {
        AttributeCondition existingAttributeCondition = attributeConditionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("AttributeCondition not found with id: " + id));

        // Mettre à jour la règle si ruleId est fourni
        if (attributeConditionDTO.getRuleId() != null) {
            Rule rule = ruleRepository.findById(attributeConditionDTO.getRuleId())
                    .orElseThrow(() -> new RuntimeException("Rule not found with id: " + attributeConditionDTO.getRuleId()));
            existingAttributeCondition.setRule(rule);
        } else {
            existingAttributeCondition.setRule(null);
        }

        // Mettre à jour la molécule si moleculeId est fourni
        if (attributeConditionDTO.getMoleculeId() != null) {
            Molecule molecule = moleculeRepository.findById(attributeConditionDTO.getMoleculeId())
                    .orElseThrow(() -> new RuntimeException("Molecule not found with id: " + attributeConditionDTO.getMoleculeId()));
            existingAttributeCondition.setMolecule(molecule);
        } else {
            existingAttributeCondition.setMolecule(null);
        }

        existingAttributeCondition.setScope(attributeConditionDTO.getScope());
        existingAttributeCondition.setAttribute(attributeConditionDTO.getAttribute());
        existingAttributeCondition.setOperator(attributeConditionDTO.getOperator());
        existingAttributeCondition.setValue(attributeConditionDTO.getValue());
        existingAttributeCondition.setUnit(attributeConditionDTO.getUnit());

        AttributeCondition updatedAttributeCondition = attributeConditionRepository.save(existingAttributeCondition);
        return convertToDTO(updatedAttributeCondition);
    }

    @Override
    public void deleteAttributeCondition(Long id) {
        if (!attributeConditionRepository.existsById(id)) {
            throw new RuntimeException("AttributeCondition not found with id: " + id);
        }
        attributeConditionRepository.deleteById(id);
    }

    @Override
    public List<AttributeConditionDTO> searchAttributeConditions(String attribute) {
        return attributeConditionRepository.findByAttributeContainingIgnoreCase(attribute)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<AttributeConditionDTO> getAttributeConditionsByScope(Scope scope) {
        return attributeConditionRepository.findByScope(scope)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<AttributeConditionDTO> getAttributeConditionsByRule(Long ruleId) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + ruleId));

        return attributeConditionRepository.findByRuleId(ruleId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public AttributeConditionDTO assignRuleToAttributeCondition(Long attributeConditionId, Long ruleId) {
        AttributeCondition attributeCondition = attributeConditionRepository.findById(attributeConditionId)
                .orElseThrow(() -> new RuntimeException("AttributeCondition not found with id: " + attributeConditionId));

        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + ruleId));

        attributeCondition.setRule(rule);
        AttributeCondition updatedAttributeCondition = attributeConditionRepository.save(attributeCondition);
        return convertToDTO(updatedAttributeCondition);
    }

    @Override
    public AttributeConditionDTO removeRuleFromAttributeCondition(Long attributeConditionId) {
        AttributeCondition attributeCondition = attributeConditionRepository.findById(attributeConditionId)
                .orElseThrow(() -> new RuntimeException("AttributeCondition not found with id: " + attributeConditionId));

        attributeCondition.setRule(null);
        AttributeCondition updatedAttributeCondition = attributeConditionRepository.save(attributeCondition);
        return convertToDTO(updatedAttributeCondition);
    }

    @Override
    public List<AttributeConditionDTO> getAttributeConditionsByMolecule(Long moleculeId) {
        Molecule molecule = moleculeRepository.findById(moleculeId)
                .orElseThrow(() -> new RuntimeException("Molecule not found with id: " + moleculeId));

        return attributeConditionRepository.findByMoleculeId(moleculeId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public AttributeConditionDTO assignMoleculeToAttributeCondition(Long attributeConditionId, Long moleculeId) {
        AttributeCondition attributeCondition = attributeConditionRepository.findById(attributeConditionId)
                .orElseThrow(() -> new RuntimeException("AttributeCondition not found with id: " + attributeConditionId));

        Molecule molecule = moleculeRepository.findById(moleculeId)
                .orElseThrow(() -> new RuntimeException("Molecule not found with id: " + moleculeId));

        attributeCondition.setMolecule(molecule);
        AttributeCondition updatedAttributeCondition = attributeConditionRepository.save(attributeCondition);
        return convertToDTO(updatedAttributeCondition);
    }

    @Override
    public AttributeConditionDTO removeMoleculeFromAttributeCondition(Long attributeConditionId) {
        AttributeCondition attributeCondition = attributeConditionRepository.findById(attributeConditionId)
                .orElseThrow(() -> new RuntimeException("AttributeCondition not found with id: " + attributeConditionId));

        attributeCondition.setMolecule(null);
        AttributeCondition updatedAttributeCondition = attributeConditionRepository.save(attributeCondition);
        return convertToDTO(updatedAttributeCondition);
    }

    @Override
    public Long countAttributeConditionsByRule(Long ruleId) {
        return attributeConditionRepository.countByRuleId(ruleId);
    }

    private AttributeConditionDTO convertToDTO(AttributeCondition attributeCondition) {
        return AttributeConditionDTO.builder()
                .id(attributeCondition.getId())
                .scope(attributeCondition.getScope())
                .attribute(attributeCondition.getAttribute())
                .operator(attributeCondition.getOperator())
                .value(attributeCondition.getValue())
                .unit(attributeCondition.getUnit())
                .ruleId(attributeCondition.getRule() != null ? attributeCondition.getRule().getId() : null)
                .ruleLabel(attributeCondition.getRule() != null ? attributeCondition.getRule().getLabel() : null)
                .moleculeId(attributeCondition.getMolecule() != null ? attributeCondition.getMolecule().getId() : null)
                .moleculeName(attributeCondition.getMolecule() != null ? attributeCondition.getMolecule().getName() : null)
                .build();
    }

    private AttributeCondition convertToEntity(AttributeConditionDTO dto) {
        AttributeCondition.AttributeConditionBuilder builder = AttributeCondition.builder()
                .scope(dto.getScope())
                .attribute(dto.getAttribute())
                .operator(dto.getOperator())
                .value(dto.getValue())
                .unit(dto.getUnit());

        // Associer la règle si ruleId est fourni
        if (dto.getRuleId() != null) {
            Rule rule = ruleRepository.findById(dto.getRuleId())
                    .orElseThrow(() -> new RuntimeException("Rule not found with id: " + dto.getRuleId()));
            builder.rule(rule);
        }

        // Associer la molécule si moleculeId est fourni
        if (dto.getMoleculeId() != null) {
            Molecule molecule = moleculeRepository.findById(dto.getMoleculeId())
                    .orElseThrow(() -> new RuntimeException("Molecule not found with id: " + dto.getMoleculeId()));
            builder.molecule(molecule);
        }

        return builder.build();
    }
}