package fr._il.MedFall.DTO;

import fr._il.MedFall.enums.RiskLevel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskEffectDTO {
    private Long id;
    private Integer score;
    private RiskLevel riskLevel;
    private String justification;
    private Long ruleId;
    private String ruleLabel;
}