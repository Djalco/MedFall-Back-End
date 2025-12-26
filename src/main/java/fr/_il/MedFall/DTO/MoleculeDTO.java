package fr._il.MedFall.DTO;


import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoleculeDTO {
    private Long id;
    private String name;
    private Float halfLifeHours;
    private Integer standardDurationWeeks;
    private AttributeConditionDTO attributeCondition;
    private Set<Long> medicationClassIds;
    private Integer medicationClassCount;
}