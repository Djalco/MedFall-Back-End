package fr._il.MedFall.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDTO {
    private Long id;
    private String textString;
    private Long ruleId;
    private String ruleLabel;
}