package fr._il.MedFall.servicesImpl;

import fr._il.MedFall.DTO.RiskEffectDTO;
import fr._il.MedFall.entities.RiskEffect;
import fr._il.MedFall.entities.Rule;
import fr._il.MedFall.repositories.RiskEffectRepository;
import fr._il.MedFall.repositories.RuleRepository;
import fr._il.MedFall.services.RiskEffectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RiskEffectServiceImpl implements RiskEffectService {

    private final RiskEffectRepository riskEffectRepository;
    private final RuleRepository ruleRepository;

    // =========================================================
    // CRUD DE BASE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<RiskEffectDTO> getAllRiskEffects() {
        return riskEffectRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RiskEffectDTO getRiskEffectById(Long id) {
        RiskEffect entity = riskEffectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RiskEffect with ID " + id + " not found"));
        return toDto(entity);
    }

    @Override
    public RiskEffectDTO createRiskEffect(RiskEffectDTO riskEffectDTO) {
        RiskEffect entity = toEntity(riskEffectDTO);
        entity = riskEffectRepository.save(entity);
        return toDto(entity);
    }

    @Override
    public RiskEffectDTO updateRiskEffect(Long id, RiskEffectDTO riskEffectDTO) {
        RiskEffect existing = riskEffectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RiskEffect with ID " + id + " not found for update"));

        if (riskEffectDTO.getScore() != null) {
            existing.setScore(riskEffectDTO.getScore());
        }
        if (riskEffectDTO.getRiskLevel() != null) {
            existing.setRiskLevel(riskEffectDTO.getRiskLevel());
        }
        if (riskEffectDTO.getJustification() != null) {
            existing.setJustification(riskEffectDTO.getJustification());
        }
        // Mise à jour de la relation Rule si nécessaire
        if (riskEffectDTO.getRuleId() != null &&
                (existing.getRule() == null || !riskEffectDTO.getRuleId().equals(existing.getRule().getId()))) {
            Rule rule = ruleRepository.findById(riskEffectDTO.getRuleId())
                    .orElseThrow(() -> new RuntimeException("Rule with ID " + riskEffectDTO.getRuleId() + " not found"));
            existing.setRule(rule);
        }

        RiskEffect updated = riskEffectRepository.save(existing);
        return toDto(updated);
    }

    @Override
    public void deleteRiskEffect(Long id) {
        if (!riskEffectRepository.existsById(id)) {
            throw new RuntimeException("RiskEffect with ID " + id + " not found for deletion");
        }
        riskEffectRepository.deleteById(id);
    }

    // =========================================================
    // MÉTHODES LIÉES À LA RULE
    // =========================================================

    @Transactional(readOnly = true)
    public List<RiskEffectDTO> getRiskEffectsByRuleId(Long ruleId) {
        return riskEffectRepository.findByRule_Id(ruleId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void deleteRiskEffectsByRuleId(Long ruleId) {
        riskEffectRepository.deleteByRule_Id(ruleId);
    }

    // =========================================================
    // MAPPERS
    // =========================================================

    private RiskEffectDTO toDto(RiskEffect entity) {
        if (entity == null) return null;

        return RiskEffectDTO.builder()
                .id(entity.getId())
                .score(entity.getScore())
                .riskLevel(entity.getRiskLevel())
                .justification(entity.getJustification())
                .ruleId(entity.getRule() != null ? entity.getRule().getId() : null)
                .ruleLabel(entity.getRule() != null ? entity.getRule().getLabel() : null)
                .build();
    }

    private RiskEffect toEntity(RiskEffectDTO dto) {
        if (dto == null) return null;

        Rule rule = null;
        if (dto.getRuleId() != null) {
            rule = ruleRepository.findById(dto.getRuleId())
                    .orElseThrow(() -> new RuntimeException("Rule with ID " + dto.getRuleId() + " not found"));
        }

        return RiskEffect.builder()
                .id(dto.getId())
                .score(dto.getScore())
                .riskLevel(dto.getRiskLevel())
                .justification(dto.getJustification())
                .rule(rule)
                .build();
    }
}
