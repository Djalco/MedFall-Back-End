package fr._il.MedFall.DTO;

import java.util.List;
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
public class MedicationClassDTO {
    private Long id;
    private String name;
    private Set<Long> moleculeIds;
    private Integer moleculeCount;
    private List<String> moleculesNames;
    private ClassCountConditionDTO classCountCondition;
}