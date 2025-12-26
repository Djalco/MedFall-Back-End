package fr._il.MedFall.DTO;

import fr._il.MedFall.enums.ConditionStrategy;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleDTO {
    private Long id;
    private String label;
    private String description;
    private ConditionStrategy strategy;
    private Long pathologyId;
    private String pathologyName;
    private List<RiskEffectDTO> riskEffects;
    private List<RecommendationDTO> recommendations;
    private List<AttributeConditionDTO> attributeConditions;
    private List<ClassCountConditionDTO> classCountConditions;
}