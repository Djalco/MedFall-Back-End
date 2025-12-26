package fr._il.MedFall.servicesImpl;

import fr._il.MedFall.DTO.RecommendationDTO;
import fr._il.MedFall.entities.Recommendation;
import fr._il.MedFall.entities.Rule;
import fr._il.MedFall.repositories.RecommendationRepository;
import fr._il.MedFall.repositories.RuleRepository;
import fr._il.MedFall.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RuleRepository ruleRepository;

    // =========================================================
    // CRUD DE BASE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDTO> getAllRecommendations() {
        return recommendationRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendationDTO getRecommendationById(Long id) {
        Recommendation entity = recommendationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recommendation with ID " + id + " not found"));
        return toDto(entity);
    }

    @Override
    public RecommendationDTO createRecommendation(RecommendationDTO dto) {
        Recommendation entity = toEntity(dto);
        entity = recommendationRepository.save(entity);
        return toDto(entity);
    }

    @Override
    public RecommendationDTO updateRecommendation(Long id, RecommendationDTO dto) {
        Recommendation existing = recommendationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recommendation with ID " + id + " not found for update"));

        if (dto.getTextString() != null) {
            existing.setTextString(dto.getTextString());
        }

        // Mise à jour de la relation Rule si nécessaire
        if (dto.getRuleId() != null &&
                (existing.getRule() == null || !dto.getRuleId().equals(existing.getRule().getId()))) {
            Rule rule = ruleRepository.findById(dto.getRuleId())
                    .orElseThrow(() -> new RuntimeException("Rule with ID " + dto.getRuleId() + " not found"));
            existing.setRule(rule);
        }

        Recommendation updated = recommendationRepository.save(existing);
        return toDto(updated);
    }

    @Override
    public void deleteRecommendation(Long id) {
        if (!recommendationRepository.existsById(id)) {
            throw new RuntimeException("Recommendation with ID " + id + " not found for deletion");
        }
        recommendationRepository.deleteById(id);
    }

    // =========================================================
    // MÉTHODES LIÉES À RULE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<RecommendationDTO> getRecommendationsByRuleId(Long ruleId) {
        return recommendationRepository.findByRule_Id(ruleId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRecommendationsByRuleId(Long ruleId) {
        recommendationRepository.deleteByRule_Id(ruleId);
    }

    // =========================================================
    // MAPPERS
    // =========================================================

    private RecommendationDTO toDto(Recommendation entity) {
        if (entity == null) return null;

        return RecommendationDTO.builder()
                .id(entity.getId())
                .textString(entity.getTextString())
                .ruleId(entity.getRule() != null ? entity.getRule().getId() : null)
                .ruleLabel(entity.getRule() != null ? entity.getRule().getLabel() : null)
                .build();
    }

    private Recommendation toEntity(RecommendationDTO dto) {
        if (dto == null) return null;

        Rule rule = null;
        if (dto.getRuleId() != null) {
            rule = ruleRepository.findById(dto.getRuleId())
                    .orElseThrow(() -> new RuntimeException("Rule with ID " + dto.getRuleId() + " not found"));
        }

        return Recommendation.builder()
                .id(dto.getId())
                .textString(dto.getTextString())
                .rule(rule)
                .build();
    }
}
