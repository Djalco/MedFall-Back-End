package fr._il.MedFall.servicesImpl;

import fr._il.MedFall.DTO.RuleDTO;
import fr._il.MedFall.entities.Pathology;
import fr._il.MedFall.entities.Rule;
import fr._il.MedFall.enums.ConditionStrategy;
import fr._il.MedFall.repositories.PathologyRepository;
import fr._il.MedFall.repositories.RuleRepository;
import fr._il.MedFall.services.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {

    private final RuleRepository ruleRepository;
    private final PathologyRepository pathologyRepository;

    @Override
    public List<RuleDTO> getAllRules() {
        return ruleRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public RuleDTO getRuleById(Long id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));
        return convertToDTO(rule);
    }

    @Override
    public RuleDTO getRuleByLabel(String label) {
        Rule rule = ruleRepository.findByLabel(label)
                .orElseThrow(() -> new RuntimeException("Rule not found with label: " + label));
        return convertToDTO(rule);
    }

    @Override
    public RuleDTO createRule(RuleDTO ruleDTO) {
        if (ruleRepository.existsByLabel(ruleDTO.getLabel())) {
            throw new RuntimeException("Rule with label '" + ruleDTO.getLabel() + "' already exists");
        }

        Rule rule = convertToEntity(ruleDTO);
        Rule savedRule = ruleRepository.save(rule);
        return convertToDTO(savedRule);
    }

    @Override
    public RuleDTO updateRule(Long id, RuleDTO ruleDTO) {
        Rule existingRule = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));

        if (!existingRule.getLabel().equals(ruleDTO.getLabel()) &&
                ruleRepository.existsByLabel(ruleDTO.getLabel())) {
            throw new RuntimeException("Rule with label '" + ruleDTO.getLabel() + "' already exists");
        }

        // Mettre à jour la pathologie si pathologyId est fourni
        if (ruleDTO.getPathologyId() != null) {
            Pathology pathology = pathologyRepository.findById(ruleDTO.getPathologyId())
                    .orElseThrow(() -> new RuntimeException("Pathology not found with id: " + ruleDTO.getPathologyId()));
            existingRule.setPathology(pathology);
        } else {
            existingRule.setPathology(null);
        }

        existingRule.setLabel(ruleDTO.getLabel());
        existingRule.setDescription(ruleDTO.getDescription());
        existingRule.setStrategy(ruleDTO.getStrategy());

        Rule updatedRule = ruleRepository.save(existingRule);
        return convertToDTO(updatedRule);
    }

    @Override
    public void deleteRule(Long id) {
        if (!ruleRepository.existsById(id)) {
            throw new RuntimeException("Rule not found with id: " + id);
        }
        ruleRepository.deleteById(id);
    }

    @Override
    public List<RuleDTO> searchRules(String label) {
        return ruleRepository.findByLabelContainingIgnoreCase(label)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<RuleDTO> getRulesByStrategy(ConditionStrategy strategy) {
        return ruleRepository.findByStrategy(strategy)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<RuleDTO> getRulesByPathology(Long pathologyId) {
        Pathology pathology = pathologyRepository.findById(pathologyId)
                .orElseThrow(() -> new RuntimeException("Pathology not found with id: " + pathologyId));

        return ruleRepository.findByPathologyId(pathologyId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public RuleDTO assignPathologyToRule(Long ruleId, Long pathologyId) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + ruleId));

        Pathology pathology = pathologyRepository.findById(pathologyId)
                .orElseThrow(() -> new RuntimeException("Pathology not found with id: " + pathologyId));

        rule.setPathology(pathology);
        Rule updatedRule = ruleRepository.save(rule);
        return convertToDTO(updatedRule);
    }

    @Override
    public RuleDTO removePathologyFromRule(Long ruleId) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + ruleId));

        rule.setPathology(null);
        Rule updatedRule = ruleRepository.save(rule);
        return convertToDTO(updatedRule);
    }

    @Override
    public Long countRulesByPathology(Long pathologyId) {
        return ruleRepository.countByPathologyId(pathologyId);
    }

    private RuleDTO convertToDTO(Rule rule) {
        return RuleDTO.builder()
                .id(rule.getId())
                .label(rule.getLabel())
                .description(rule.getDescription())
                .strategy(rule.getStrategy())
                .pathologyId(rule.getPathology() != null ? rule.getPathology().getId() : null)
                .pathologyName(rule.getPathology() != null ? rule.getPathology().getName() : null)
                .build();
    }

    private Rule convertToEntity(RuleDTO dto) {
        Rule.RuleBuilder builder = Rule.builder()
                .label(dto.getLabel())
                .description(dto.getDescription())
                .strategy(dto.getStrategy());

        // Associer la pathologie si pathologyId est fourni
        if (dto.getPathologyId() != null) {
            Pathology pathology = pathologyRepository.findById(dto.getPathologyId())
                    .orElseThrow(() -> new RuntimeException("Pathology not found with id: " + dto.getPathologyId()));
            builder.pathology(pathology);
        }

        return builder.build();
    }
}