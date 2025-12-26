package fr._il.MedFall.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassCountConditionDTO {
    private Long id;
    private String comparator;
    private Integer threshold;
    private Boolean distinctMolecules;
    private Long ruleId;
    private String ruleLabel;
    private Long medicationClassId;
    private String medicationClassName;
}